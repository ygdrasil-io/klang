package klang.parser.libclang.panama

import klang.domain.NativeStructure
import klang.domain.TypeRef
import org.openjdk.jextract.Declaration

internal fun Declaration.Scoped.toNativeStructure(name: String?, isUnion: Boolean = false) = NativeStructure(
	name ?: name(),
	members().toStructureFields(),
	isUnion
)

private fun List<Declaration>.toStructureFields(): List<Pair<String, TypeRef>> = filterIsInstance<Declaration.Variable>()
	.map { it.toStructureField() }

private fun Declaration.Variable.toStructureField() = name() to type().toTypeRef()