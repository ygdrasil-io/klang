package klang.parser.libclang.panama

import klang.domain.NativeEnumeration
import org.openjdk.jextract.Declaration

internal fun Declaration.Scoped.toNativeEnumeration() = NativeEnumeration(
	name(),
	members().toEnumValues()
)

private fun List<Declaration>.toEnumValues(): List<Pair<String, Long>> = filterIsInstance<Declaration.Constant>()
	.map {
		it.name() to it.value().toString().toLong()
	}