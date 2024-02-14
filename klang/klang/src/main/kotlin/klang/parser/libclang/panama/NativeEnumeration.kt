package klang.parser.libclang.panama

import klang.domain.DeclarationOrigin
import klang.domain.NativeEnumeration
import klang.domain.NotBlankString
import org.openjdk.jextract.Declaration

internal fun Declaration.Scoped.toNativeEnumeration(name: String?, origin: DeclarationOrigin) = NativeEnumeration(
	NotBlankString(name ?: name()),
	members().toEnumValues(),
	source = origin
)

private fun List<Declaration>.toEnumValues(): List<Pair<String, Long>> = filterIsInstance<Declaration.Constant>()
	.map {
		it.name() to it.value().toString().toLong()
	}