package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.*

internal fun NativeStructure.toSpec(packageName: String) = ClassName("", name)
	.let { structureClass ->
		when {
			fields.isEmpty() -> toSpecWithNoAttributes(structureClass)
			isUnion -> toUnionSpec(packageName, structureClass)
			else -> toSpecWithAttributes(packageName, structureClass)
		}
	}

private fun NativeStructure.toUnionSpec(packageName: String, structureClass: ClassName) =
	TypeSpec.classBuilder(structureClass)
		.addModifiers(KModifier.OPEN)
		.superclass(jnaUnion)
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
		.addFunction(
			FunSpec.builder("read")
				.addModifiers(KModifier.OVERRIDE)
				.addStatement("$packageName.${structureClass}Delegate.read(this)")
				.addStatement("super.read()")
				.build()
		)
		.addRefAndValueClass(structureClass)
		.build()

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
			.addRefAndValueClass(structureClass)
			.build()

private fun TypeSpec.Builder.addRefAndValueClass(structureClass: ClassName): TypeSpec.Builder = this
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

private fun propertySpec(
	name: String,
	typeRef: TypeRef,
	packageName: String
): PropertySpec = when (typeRef) {
	is ResolvedTypeRef -> typeRef.toPropertySpec(name, packageName)
	else -> typeRef.defaultPropertySpec(name)
}

private fun ResolvedTypeRef.toPropertySpec(
	name: String,
	packageName: String
): PropertySpec {

	val rootType = type.rootType()

	val type = when {
		rootType is NativeStructure -> toType(packageName)
		rootType is FunctionPointerType -> jnaCallback.copy(nullable = true)
		rootType is StringType -> toType(packageName)
		rootType is PrimitiveType && isPointer.not() -> toType(packageName)
		rootType is NativeEnumeration && isPointer.not() -> when (rootType.type) {
			is ResolvedTypeRef -> rootType.type.toType(packageName)
			else -> null
		}
		else ->  null
	} ?: jnaPointer.copy(nullable = true)

	val defaultValue = when {
		rootType is NativeStructure -> "${rootType.name}()"
		rootType is StringType -> "\"\""
		isPointer -> "null"
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
		.addKdoc("mapped from ${referenceAsString}")
		.addAnnotation(jnaJvmField)
		.initializer(defaultValue)
		.mutable(true)
		.build()
}

private fun TypeRef.defaultPropertySpec(
	name: String
): PropertySpec {
	return PropertySpec
		.builder(name, jnaPointer.copy(nullable = true))
		.addKdoc("mapped from $referenceAsString")
		.addAnnotation(jnaJvmField)
		.initializer("null")
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