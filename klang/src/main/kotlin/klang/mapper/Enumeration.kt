package klang.mapper

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import klang.domain.NativeEnumeration

// TODO: switch to internal
public fun NativeEnumeration.toSpec() = ClassName("", name)
	.let { enumerationClass ->
		val valueType = ClassName("", "kotlin.Long")
		val valueName = "nativeValue"
		TypeSpec.enumBuilder(enumerationClass)
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
							.returns(enumerationClass.copy(nullable = true))
							.addStatement("return entries.find { it.$valueName == $valueName }")
							.build()
					)
					.build()
			)
			.apply {
				values.forEach { (name, value) ->
					addEnumConstant(
						name, TypeSpec.anonymousClassBuilder()
							.addSuperclassConstructorParameter("%L", value)
							.build()
					)
				}
			}.build()
	}