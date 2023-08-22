package klang.parser.domain.type


class Qualified(kind: Kind, override val type: Typed, name: String = "") : Delegated(kind, name)


/**
 * Creates a new typedef type given name and underlying type.
 * @param name the typedef type name.
 * @param aliased the typeef type underlying type.
 * @return a new typedef type with given name and underlying type.
 */
fun typedef(name: String, aliased: Typed): Delegated {
    return Qualified(Delegated.Kind.TYPEDEF, aliased, name)
}