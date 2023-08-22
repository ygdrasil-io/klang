package klang.parser.domain.type

class Pointer(val pointeeFactory: () -> Typed): Delegated(Kind.POINTER, "") {

    constructor(canonicalType: Typed) : this({canonicalType})

    override val type: Typed
        get() = pointeeFactory()


}