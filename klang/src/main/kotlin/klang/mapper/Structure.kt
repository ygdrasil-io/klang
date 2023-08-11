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
					.addMember(fields.joinToString(", ") { "${it.first}" })
					.build()
			)
			.addModifiers(KModifier.OPEN)
			.addSuperinterface(jnaStructure)
			.addSuperclassConstructorParameter("pointer")
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter("pointer", jnaPointer.copy(nullable = true))
					.build()
			)
			.addProperty(
				PropertySpec.builder(valueName, valueType)
					.initializer(valueName)
					.build()
			)
			.apply {

			}.build()
	}