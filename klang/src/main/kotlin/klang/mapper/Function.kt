package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeFunction

internal fun List<NativeFunction>.toInterfaceSpec(name: String) = ClassName("", name)
	.let { interfaceClass ->
		TypeSpec.interfaceBuilder(interfaceClass)
			.addFunctions(map { it.toSpec() })
			.build()
	}

private fun NativeFunction.toSpec() = FunSpec
	.builder(name)
	.addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
	.returns(ClassName("", returnType))
	.addParameters(arguments.map { it.toSpec() })
	.build()


private fun NativeFunction.Argument.toSpec() = ParameterSpec
	.builder(name ?: "", ClassName("", type))
	.build()