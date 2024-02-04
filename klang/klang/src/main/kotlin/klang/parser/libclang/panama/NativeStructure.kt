package klang.parser.libclang.panama

import klang.domain.NativeStructure
import klang.domain.TypeRef
import org.openjdk.jextract.Declaration

internal fun Declaration.Scoped.toNativeStructure(name: String?, isUnion: Boolean = false) = Triple(
	name ?: name(),
	members().toStructureFields(),
	isUnion
).let { (name, fields, isUnion) ->
	NativeStructure(
		name,
		fields,
		isUnion
	)

}

private fun List<Declaration>.toStructureFields(): List<Pair<String, TypeRef>> = filterIsInstance<Declaration.Variable>()
	.map { it.toStructureField() }

private fun Declaration.Variable.toStructureField() = (name() to type().toTypeRef())
	.let { (name, ref) ->
		name to ref
	}