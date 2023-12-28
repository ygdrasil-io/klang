package klang.parser.libclang.panama

import klang.domain.NativeStructure
import klang.domain.TypeRef
import org.openjdk.jextract.Declaration

internal fun Declaration.Scoped.toNativeStructure(isUnion: Boolean = false) = NativeStructure(
	name(),
	members().toStructureField(),
	isUnion
)

private fun List<Declaration>.toStructureField(): List<Pair<String, TypeRef>> = filterIsInstance<Declaration.Variable>()
	.map { it.name() to it.type().toTypeRef() }