package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.*

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
				fields.forEach { (name, typeRef) ->
					addProperty(
						propertySpec(name, typeRef, packageName)
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

private fun propertySpec(
	name: String,
	typeRef: TypeRef,
	packageName: String
): PropertySpec {

	val rootType = when {
		typeRef.isPointer -> null
		typeRef is ResolvedTypeRef -> typeRef.type.rootType()
		else -> null
	}

	val type = when(rootType) {
		is PrimitiveType -> typeRef.toType(packageName)
		is NativeEnumeration -> when (rootType.type) {
			is ResolvedTypeRef -> rootType.type.toType(packageName)
			else -> null
		}
		else -> null
	} ?: jnaPointer.copy(nullable = true)

	val defaultValue = when {
		rootType is FixeSizeType -> when {
			rootType.isFloating && rootType.size == 32 -> "0.0f"
			rootType.isFloating && rootType.size == 64 -> "0.0"
			else -> "0"
		}
		rootType is PlatformDependantSizeType -> when {
			rootType.size == 32..64 -> "com.sun.jna.NativeLong(0)"
			else -> "null"
		}
		else -> "null"
	}

	return PropertySpec
		.builder(name, type)
		.addKdoc("mapped from ${typeRef.referenceAsString}")
		.addAnnotation(jnaJvmField)
		.initializer(defaultValue)
		.mutable(true)
		.build()
}


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