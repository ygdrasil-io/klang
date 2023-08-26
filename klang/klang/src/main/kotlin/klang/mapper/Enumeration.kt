package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeEnumeration

internal fun NativeEnumeration.toSpecAsEnumeration(packageName: String) = ClassName(packageName, name)
	.let { enumerationClass ->
		val valueType = type.toType(packageName)
		val valueName = "value"
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
			.addFunction(
				FunSpec.builder("or")
					.addModifiers(KModifier.INFIX)
					.addParameter("other", valueType)
					.returns(valueType)
					.addStatement("return value or other")
					.build()
			)
			.addFunction(
				FunSpec.builder("or")
					.addModifiers(KModifier.INFIX)
					.addParameter("other", enumerationClass)
					.returns(valueType)
					.addStatement("return value or other.value")
					.build()
			)
			.addType(
				TypeSpec.companionObjectBuilder()
					.addFunction(
						FunSpec.builder("of")
							.addParameter(valueName, valueType)
							.returns(enumerationClass.copy(nullable = true))
							.beginControlFlow("return entries.find")
							.addStatement("it.$valueName == $valueName ")
							.endControlFlow()
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