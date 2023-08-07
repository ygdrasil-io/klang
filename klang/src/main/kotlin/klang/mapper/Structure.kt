package klang.mapper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeStructure

internal fun NativeStructure.toSpec() = ClassName("", name)
	.let { structureClass ->
		val valueType = ClassName("", "Long")
		val valueName = "nativeValue"
		TypeSpec.classBuilder(structureClass)
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter(valueName, valueType)
					.build()
			)
			.addProperty(
				PropertySpec.builder(valueName, valueType)
					.initializer(valueName)
					.build()
			)
			.addType(
				TypeSpec.companionObjectBuilder()
					.addFunction(
						FunSpec.builder("of")
							.addParameter(valueName, valueType)
							.returns(structureClass.copy(nullable = true))
							.addStatement("return entries.find { it.$valueName == $valueName }")
							.build()
					)
					.build()
			)
			.apply {

			}.build()
	}