package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeStructure

internal fun NativeStructure.toSpec() = ClassName("", name)
	.let { structureClass ->
		val valueType = ClassName("", "Long")
		val valueName = "nativeValue"
		TypeSpec.classBuilder(structureClass)
			.addAnnotation(
				AnnotationSpec.builder(jnaFieldOrder)
					.addMember(fields.joinToString(", ") { "\"${it.first}\"" })
					.build()
			)
			.addModifiers(KModifier.OPEN)
			.addSuperinterface(jnaStructure)
			.addSuperclassConstructorParameter("pointer")
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter(
						ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
							.defaultValue("null")
							.build()
					)
					.build()
			)
			.apply {
				fields.forEach { (name, type) ->
					addProperty(
						PropertySpec.builder(name, ClassName("", type))
							.addAnnotation(jnaJvmField)
							.initializer("0")
							.mutable(true)
							.build()
					)
				}
			}.build()
	}