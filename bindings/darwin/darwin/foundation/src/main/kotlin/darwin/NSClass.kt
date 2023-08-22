package darwin

open class NSClass(val name: String) : NSObject(ObjectiveC.objc_getClass(name)) {
	val OBJ_CLASS = id
}