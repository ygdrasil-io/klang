package klang.parser.libclang.panama

import klang.domain.NameableDeclaration
import klang.domain.NativeFunction
import org.openjdk.jextract.Declaration
import org.openjdk.jextract.Declaration.Variable

internal fun Declaration.Function.toLocalDeclaration(): NameableDeclaration = NativeFunction(
	name(),
	returnType = type().toTypeRef(),
	arguments = parameters().map { it.toArgument() }
)

private fun Variable.toArgument() = NativeFunction.Argument(
	name(),
	type().toTypeRef()
)