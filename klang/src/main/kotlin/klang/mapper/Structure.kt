package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeStructure

internal fun NativeStructure.toSpec(packageName: String) = ClassName("", name)
	.let { structureClass ->
		TypeSpec.classBuilder(structureClass)
			.addAnnotation(
				AnnotationSpec.builder(jnaFieldOrder)
					.addMember(fields.joinToString(", ") { "\"${it.first}\"" })
					.build()
			)
			.addModifiers(KModifier.OPEN)
			.superclass(jnaStructure)
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
						PropertySpec.builder(name, type.toType(packageName))
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
					.superclass(structureClass)
					.primaryConstructor(
						FunSpec.constructorBuilder()
							.addParameter(
								ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
									.defaultValue("null")
									.build()
							)
							.build()
					)
					.addSuperclassConstructorParameter("pointer")
					.addSuperinterface(jnaByReference)
					.build()
			)
			.addType(
				TypeSpec.classBuilder("ByValue")
					.superclass(structureClass)
					.primaryConstructor(
						FunSpec.constructorBuilder()
							.addParameter(
								ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
									.defaultValue("null")
									.build()
							)
							.build()
					)
					.addSuperclassConstructorParameter("pointer")
					.addSuperinterface(jnaByValue)
					.build()
			)
			.build()
	}