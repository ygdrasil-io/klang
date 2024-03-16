package klang.mapper

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.FunctionPointerType

internal fun FunctionPointerType.toCallbackSpec(name: String, packageName: String) = TypeSpec.interfaceBuilder(name)
	.addSuperinterface(jnaCallback)
	.addFunction(
		FunSpec.builder("invoke")
			.addModifiers(KModifier.OPERATOR)
			.addModifiers(KModifier.ABSTRACT)
			.addParameters(
				arguments
					.map { it.toType(packageName, fromFunction = true) }
					.mapIndexed { index, type ->
						ParameterSpec
							.builder("param${index + 1}", type)
							.build()
					}
			)
			.returns(returnType.toType(packageName))
			.build()
	)
	.build()