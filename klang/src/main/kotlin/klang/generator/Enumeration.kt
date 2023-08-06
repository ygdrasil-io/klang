package klang.generator

import com.squareup.kotlinpoet.*
import klang.domain.KotlinEnumeration

internal fun KotlinEnumeration.generateCode() = """
enum class $name(val value: $type) {
${values.joinToString(separator = ",\n") { "\t${it.first}(${it.second})" }};

	companion object {
		fun of(value: $type): $name? = entries.find { it.value == value }
	}
}
""".trimIndent()


internal fun KotlinEnumeration.generateCode2() = ClassName("", name)
	.let { enumerationClass ->
		val valueType = ClassName("", type)
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


