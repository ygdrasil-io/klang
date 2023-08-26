package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeStructure

internal fun NativeStructure.toSpec(packageName: String) = ClassName("", name)
	.let { structureClass ->
		when {
			fields.isNotEmpty() -> toSpecWithAttributes(packageName, structureClass)
			else -> toSpecWithNoAttributes(structureClass)
		}
	}

private fun NativeStructure.toSpecWithAttributes(packageName: String, structureClass: ClassName) =
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
						PropertySpec
							.builder(name, type.toType(packageName, nullable = type.isPointer || type.isPrimitive.not()))
							.addKdoc("mapped from ${type.referenceAsString}")
							.addAnnotation(jnaJvmField)
							.initializer(type.defaultValue)
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


private fun toSpecWithNoAttributes(structureClass: ClassName) =
	TypeSpec.classBuilder(structureClass)
		.superclass(jnaPointerType)
		.addFunction(FunSpec.constructorBuilder().callSuperConstructor().build())
		.addFunction(
			FunSpec.constructorBuilder()
				.addParameter(
					ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
						.build()
				)
				.callSuperConstructor("pointer")
				.build()
		)
		.addType(
			TypeSpec.classBuilder("ByReference")
				.superclass(jnaPointerByReference)
				.addFunction(FunSpec.constructorBuilder().callSuperConstructor().build())
				.addFunction(
					FunSpec.constructorBuilder()
						.addParameter(
							ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
								.build()
						)
						.callSuperConstructor("pointer")
						.build()
				)
				.build()
		)
		.build()