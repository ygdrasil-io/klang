package klang.parser.domain.type

sealed class Array(
    val elementCount: Long?,
    val elementTyped: Typed
) : Typed() {

    class Vector( elementCount: Long?,  elementTyped: Typed) : Array(elementCount, elementTyped)
    class Incomplete(elementTyped: Typed) : Array(null, elementTyped)
    class Constant( elementCount: Long?,  elementTyped: Typed) : Array(elementCount, elementTyped)


}
