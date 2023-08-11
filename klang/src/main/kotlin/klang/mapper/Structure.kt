package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeStructure

val test = TypeSpec.classBuilder(jnaStructure)
	.primaryConstructor(
		FunSpec.constructorBuilder()
			.addParameter(
				ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
					.defaultValue("null")
					.build()
			)
			.build()
	).build()

internal fun NativeStructure.toSpec() = ClassName("", name)
	.let { structureClass ->
		TypeSpec.classBuilder(structureClass)
			.addAnnotation(
				AnnotationSpec.builder(jnaFieldOrder)
					.addMember(fields.joinToString(", ") { "\"${it.first}\"" })
					.build()
			)
			.addModifiers(KModifier.OPEN)
			.primaryConstructor(
				FunSpec.constructorBuilder()
					.addParameter(
						ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
							.defaultValue("null")
							.build()
					)
					.build()
			)
			.addSuperinterface(jnaStructure, "pointer")
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
				superclassConstructorParameters.add(CodeBlock.of("pointer"))
			}
			.addType(
				TypeSpec.classBuilder("ByReference")
					.primaryConstructor(
						FunSpec.constructorBuilder()
							.addParameter(
								ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
									.defaultValue("null")
									.build()
							)
							.build()
					)
					.addSuperinterface(structureClass, constructorParameter = "pointer")
					.addSuperinterface(jnaByReference)
					.build()
			)
			.addType(
				TypeSpec.classBuilder("ByValue")
					.primaryConstructor(
						FunSpec.constructorBuilder()
							.addParameter(
								ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
									.defaultValue("null")
									.build()
							)
							.build()
					)
					.addSuperinterface(structureClass, constructorParameter = "pointer")
					.addSuperinterface(jnaByValue)
					.build()
			)
			.build()
	}