package klang.jvm

import klang.jvm.binding.CXPrintingPolicy

class PrintingPolicy internal constructor(val policy: CXPrintingPolicy) : AutoCloseable {

    override fun close() {
        dispose()
    }

    fun dispose() {
        Clang.PrintingPolicy_dispose(policy)
    }

    fun getProperty(prop: PrintingPolicyProperty): Boolean {
        return Clang.PrintingPolicy_getProperty(policy, prop.ordinal) != 0
    }

}