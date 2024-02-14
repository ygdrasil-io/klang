package klang.parser.libclang.panama

import klang.domain.DeclarationOrigin
import klang.domain.NativeEnumeration
import klang.domain.notBlankString
import org.openjdk.jextract.Declaration

internal fun Declaration.Scoped.toNativeEnumeration(name: String?, origin: DeclarationOrigin) =
	notBlankString(name ?: name())?.let { it to members().toEnumValues()}
		?.let {(name, values) ->
			NativeEnumeration(
				name,
				values,
				source = origin
			)
		}


private fun List<Declaration>.toEnumValues(): List<Pair<String, Long>> = filterIsInstance<Declaration.Constant>()
	.map {
		it.name() to it.value().toString().toLong()
	}