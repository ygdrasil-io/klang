package libclang

import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

object RuntimeHelper {
    private val LINKER = Linker.nativeLinker()
    private val LOADER = RuntimeHelper::class.java.classLoader
    private val MH_LOOKUP = MethodHandles.lookup()
    private val SYMBOL_LOOKUP: SymbolLookup
    private val THROWING_ALLOCATOR = SegmentAllocator { x: Long, y: Long -> throw AssertionError("should not reach here") }
    val CONSTANT_ALLOCATOR = SegmentAllocator { size: Long, align: Long -> MemorySegment.allocateNative(size, align, SegmentScope.auto()) }

    init {
        // Manual change to handle platform specific library name difference
        val libName = if (System.getProperty("os.name").startsWith("Windows")) "libclang" else "clang"
        System.loadLibrary(libName)
        val loaderLookup = SymbolLookup.loaderLookup()
        SYMBOL_LOOKUP = SymbolLookup { name: String? -> loaderLookup.find(name).or { LINKER.defaultLookup().find(name) } }
    }

    fun <T> requireNonNull(obj: T?, symbolName: String): T {
        if (obj == null) {
            throw UnsatisfiedLinkError("unresolved symbol: $symbolName")
        }
        return obj
    }

    fun lookupGlobalVariable(name: String?, layout: MemoryLayout): MemorySegment? {
        return SYMBOL_LOOKUP.find(name).map { symbol: MemorySegment -> MemorySegment.ofAddress(symbol.address(), layout.byteSize(), symbol.scope()) }.orElse(null)
    }

    fun downcallHandle(name: String?, fdesc: FunctionDescriptor?): MethodHandle? {
        return SYMBOL_LOOKUP.find(name).map { addr: MemorySegment? -> LINKER.downcallHandle(addr, fdesc) }.orElse(null)
    }

    fun downcallHandle(fdesc: FunctionDescriptor?): MethodHandle {
        return LINKER.downcallHandle(fdesc)
    }

    fun downcallHandleVariadic(name: String?, fdesc: FunctionDescriptor): MethodHandle? {
        return SYMBOL_LOOKUP.find(name).map { addr: MemorySegment? -> VarargsInvoker.make(addr, fdesc) }.orElse(null)
    }

    fun <Z> upcallStub(fi: Class<Z>?, z: Z, fdesc: FunctionDescriptor, scope: SegmentScope?): MemorySegment {
        return try {
            var handle = MH_LOOKUP.findVirtual(fi, "apply", fdesc.toMethodType())
            handle = handle.bindTo(z)
            LINKER.upcallStub(handle, fdesc, scope)
        } catch (ex: Throwable) {
            throw AssertionError(ex)
        }
    }

    fun asArray(addr: MemorySegment, layout: MemoryLayout, numElements: Int, scope: SegmentScope?): MemorySegment {
        return MemorySegment.ofAddress(addr.address(), numElements * layout.byteSize(), scope)
    }

    // Internals only below this point
    private class VarargsInvoker private constructor(private val symbol: MemorySegment?, private val function: FunctionDescriptor) {
        @Throws(Throwable::class)
        private operator fun invoke(allocator: SegmentAllocator, args: Array<Any>): Any {
            // one trailing Object[]
            val nNamedArgs = function.argumentLayouts().size
            assert(args.size == nNamedArgs + 1)
            // The last argument is the array of vararg collector
            val unnamedArgs = args[args.size - 1] as Array<Any>
            val argsCount = nNamedArgs + unnamedArgs.size
            val argTypes: Array<Class<*>?> = arrayOfNulls(argsCount)
            val argLayouts = arrayOfNulls<MemoryLayout>(nNamedArgs + unnamedArgs.size)
            var pos = 0
            pos = 0
            while (pos < nNamedArgs) {
                argLayouts[pos] = function.argumentLayouts()[pos]
                pos++
            }
            assert(pos == nNamedArgs)
            for (o in unnamedArgs) {
                argLayouts[pos] = variadicLayout(normalize(o.javaClass))
                pos++
            }
            assert(pos == argsCount)
            val f = if (function.returnLayout().isEmpty) FunctionDescriptor.ofVoid(*argLayouts) else FunctionDescriptor.of(function.returnLayout().get(), *argLayouts)
            var mh = LINKER.downcallHandle(symbol, f)
            val needsAllocator = function.returnLayout().isPresent &&
                    function.returnLayout().get() is GroupLayout
            if (needsAllocator) {
                mh = mh.bindTo(allocator)
            }
            // flatten argument list so that it can be passed to an asSpreader MH
            val allArgs = arrayOfNulls<Any>(nNamedArgs + unnamedArgs.size).apply {
                System.arraycopy(args, 0, this, 0, nNamedArgs)
                System.arraycopy(unnamedArgs, 0, this, nNamedArgs, unnamedArgs.size)
            }
            return mh.asSpreader(Array<Any>::class.java, argsCount)
                    .invoke(allArgs)
        }

        private fun promote(c: Class<*>): Class<*> = when (c) {
            Byte::class.javaPrimitiveType, Char::class.javaPrimitiveType, Short::class.javaPrimitiveType, Int::class.javaPrimitiveType -> Long::class.javaPrimitiveType
            Float::class.javaPrimitiveType -> Double::class.javaPrimitiveType
            else -> c
        } ?: error("fail to promote $c")

        private fun normalize(c: Class<*>): Class<*> {
            val c: Class<*> = unboxIfNeeded(c)
            return when {
                c.isPrimitive -> promote(c)
                c == MemorySegment::class.java -> MemorySegment::class.java
                else -> throw IllegalArgumentException("Invalid type for ABI: " + c.typeName)
            }
        }

        private fun variadicLayout(c: Class<*>?): MemoryLayout {
            return if (c == Long::class.javaPrimitiveType) {
                ValueLayout.JAVA_LONG
            } else if (c == Double::class.javaPrimitiveType) {
                ValueLayout.JAVA_DOUBLE
            } else if (c == MemorySegment::class.java) {
                ValueLayout.ADDRESS
            } else {
                throw IllegalArgumentException("Unhandled variadic argument class: $c")
            }
        }

        companion object {
            private val INVOKE_MH: MethodHandle

            init {
                try {
                    INVOKE_MH = MethodHandles.lookup().findVirtual(VarargsInvoker::class.java, "invoke", MethodType.methodType(Any::class.java, SegmentAllocator::class.java, Array<Any>::class.java))
                } catch (e: ReflectiveOperationException) {
                    throw RuntimeException(e)
                }
            }

            fun make(symbol: MemorySegment?, function: FunctionDescriptor): MethodHandle {
                val invoker = VarargsInvoker(symbol, function)
                var handle = INVOKE_MH!!.bindTo(invoker).asCollector(Array<Any>::class.java, function.argumentLayouts().size + 1)
                var mtype = MethodType.methodType(if (function.returnLayout().isPresent) carrier(function.returnLayout().get(), true) else Void.TYPE)
                for (layout in function.argumentLayouts()) {
                    mtype = mtype.appendParameterTypes(carrier(layout, false))
                }
                mtype = mtype.appendParameterTypes(Array<Any>::class.java)
                val needsAllocator = function.returnLayout().isPresent &&
                        function.returnLayout().get() is GroupLayout
                if (needsAllocator) {
                    mtype = mtype.insertParameterTypes(0, SegmentAllocator::class.java)
                } else {
                    handle = MethodHandles.insertArguments(handle, 0, THROWING_ALLOCATOR)
                }
                return handle.asType(mtype)
            }

            fun carrier(layout: MemoryLayout, ret: Boolean): Class<*> {
                return if (layout is ValueLayout) {
                    layout.carrier()
                } else if (layout is GroupLayout) {
                    MemorySegment::class.java
                } else {
                    throw AssertionError("Cannot get here!")
                }
            }

            private fun unboxIfNeeded(clazz: Class<*>): Class<*> = when (clazz) {
                Boolean::class.java -> Boolean::class.javaPrimitiveType
                Void::class.java -> Void.TYPE
                Byte::class.java -> Byte::class.javaPrimitiveType
                Char::class.java -> Char::class.javaPrimitiveType
                Short::class.java -> Short::class.javaPrimitiveType
                Int::class.java -> Int::class.javaPrimitiveType
                Long::class.java -> Long::class.javaPrimitiveType
                Float::class.java -> Float::class.javaPrimitiveType
                Double::class.java -> Double::class.javaPrimitiveType
                else -> clazz
            } ?: error("Cannot unbox $clazz")
        }
    }
}
