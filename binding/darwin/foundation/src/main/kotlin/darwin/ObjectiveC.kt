package darwin

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import darwin.internal.ID
import darwin.internal.NativeLoad
import darwin.internal.NativeName

// https://developer.apple.com/documentation/objectivec/objective-c_runtime
interface ObjectiveC : Library {
	fun objc_copyProtocolList(outCount: IntArray): Pointer
	fun protocol_getName(protocol: Long): String

	fun objc_getClass(name: String): Long

	fun objc_getClassList(buffer: Pointer?, bufferCount: Int): Int

	fun objc_getProtocol(name: String): Long

	fun class_addProtocol(a: Long, b: Long): Long
	fun class_copyMethodList(clazz: Long, items: IntArray): Pointer

	// typedef struct objc_method_description {
	//     SEL name;        // The name of the method
	//     char *types;     // The types of the method arguments
	// } MethodDescription;
	fun protocol_copyMethodDescriptionList(proto: Long, isRequiredMethod: Boolean, isInstanceMethod: Boolean, outCount: IntArray): Pointer

	fun objc_registerClassPair(cls: Long)
	fun objc_lookUpClass(name: String): Long

	fun objc_msgSend(vararg args: Any?): Long
	@NativeName("objc_msgSend")
	fun objc_msgSendInt(vararg args: Any?): Int
	@NativeName("objc_msgSend")
	fun objc_msgSendVoid(vararg args: Any?): Unit
	@NativeName("objc_msgSend")
	fun objc_msgSendCGFloat(vararg args: Any?): CGFloat
	@NativeName("objc_msgSend")
	fun objc_msgSendFloat(vararg args: Any?): Float
	@NativeName("objc_msgSend_stret")
	fun objc_msgSend_stret(structPtr: Any?, vararg args: Any?)

	fun method_getName(m: Long): Long
	@NativeName("method_getName")
	fun method_getNameString(m: Long): String

	fun sel_registerName(name: String): Long
	fun sel_getName(sel: Long): String
	@NativeName("sel_getName")
	fun sel_getNameString(sel: String): String

	fun objc_allocateClassPair(clazz: Long, name: String, extraBytes: Int): Long
	fun object_getIvar(obj: Long, ivar: Long): Long

	fun class_getInstanceVariable(clazz: ID, name: String): ID
	fun class_getProperty(clazz: ID, name: String): ID

	fun class_addMethod(cls: Long, name: Long, imp: Callback, types: String): Long
	fun class_conformsToProtocol(cls: Long, protocol: Long): Boolean

	fun object_getClass(obj: ID): ID
	fun class_getName(clazz: ID): String

	fun object_getClassName(obj: ID): String
	fun class_getImageName(obj: ID): String

	fun property_getName(prop: ID): String
	fun property_getAttributes(prop: ID): String

	fun class_getInstanceMethod(cls: ID, id: NativeLong): NativeLong

	fun method_getReturnType(id: NativeLong, dst: Pointer, dst_length: NativeLong)
	fun method_getTypeEncoding(ptr: Pointer): String

	fun class_createInstance(cls: ID, extraBytes: NativeLong): ID
	fun class_copyPropertyList(cls: ID, outCountPtr: IntArray): Pointer
	fun class_copyIvarList(cls: ID, outCountPtr: IntArray): Pointer
	fun ivar_getName(ivar: Pointer?): String?
	fun ivar_getTypeEncoding(ivar: Pointer?): String?

	companion object : ObjectiveC by NativeLoad("objc")
}