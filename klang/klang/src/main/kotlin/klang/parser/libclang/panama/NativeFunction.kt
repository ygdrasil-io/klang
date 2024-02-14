package klang.parser.libclang.panama

import klang.domain.*
import org.openjdk.jextract.Declaration
import org.openjdk.jextract.Declaration.Variable

internal fun Declaration.Function.toNativeTypeAlias(origin: DeclarationOrigin): NameableDeclaration = NativeFunction(
	NotBlankString(name()),
	returnType = type().toTypeRef(),
	arguments = parameters().map { it.toArgument() },
	source = origin
)

private fun Variable.toArgument() = NativeFunction.Argument(
	notBlankString(name()),
	type().toTypeRef()
)