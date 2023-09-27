package klang.parser.domain

import klang.jvm.Type
import klang.parser.TypeMaker
import klang.parser.domain.type.Typed

data class ParsingContext(
    private val typedCache: MutableMap<Type, Typed> = mutableMapOf(),
    var unresolved: MutableList<TypeMaker.ClangTypeReference> = mutableListOf()
) {

    fun findTyped(type: Type): Typed {
        return typedCache[type] ?: Typed.Unresolved(type)
    }

    fun addTyped(type: Type, typed: Typed) {
        if (typed !is Typed.Unresolved &&
            typedCache.put(type, typed) != null) {
            throw ConcurrentModificationException()
        }
    }
}
