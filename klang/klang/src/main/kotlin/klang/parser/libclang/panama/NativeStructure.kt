package klang.parser.libclang.panama

import klang.domain.NativeStructure
import klang.domain.StructureField
import klang.domain.TypeRefField
import org.openjdk.jextract.Declaration
import org.openjdk.jextract.impl.TypeImpl

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

private fun List<Declaration>.toStructureFields(): List<StructureField> = filterIsInstance<Declaration.Variable>()
	.mapNotNull { it.toStructureField() }

private fun Declaration.Variable.toStructureField(): TypeRefField? = (name() to type())
	.let { (name, type) ->
		when {type is TypeImpl.DeclaredImpl && type.tree().kind() == Declaration.Scoped.Kind.UNION -> null
			else -> TypeRefField(name, type.toTypeRef())
		}
	}