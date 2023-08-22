package klang.parser.domain.type

abstract class Delegated(
    val kind: Kind,
    val name: String
) : Typed() {

    abstract val type: Typed

    enum class Kind {

        TYPEDEF,
        POINTER,
        SIGNED,
        UNSIGNED,
        ATOMIC,
        VOLATILE,
        COMPLEX
    }
}