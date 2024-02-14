package klang.parser.libclang.panama

import klang.domain.DeclarationOrigin
import klang.domain.NameableDeclaration
import klang.domain.NativeFunction
import klang.domain.NotBlankString
import org.openjdk.jextract.Declaration
import org.openjdk.jextract.Declaration.Variable

internal fun Declaration.Function.toNativeTypeAlias(origin: DeclarationOrigin): NameableDeclaration = NativeFunction(
	NotBlankString(name()),
	returnType = type().toTypeRef(),
	arguments = parameters().map { it.toArgument() },
	source = origin
)

private fun Variable.toArgument() = NativeFunction.Argument(
	name(),
	type().toTypeRef()
)