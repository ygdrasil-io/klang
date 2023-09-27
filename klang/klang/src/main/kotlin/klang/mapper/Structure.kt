package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.*

internal fun NativeStructure.toSpec(packageName: String) = ClassName("", name)
	.let { structureClass ->
		generateFunctionPointerTypeInterface(packageName) + when {
			fields.isEmpty() -> toSpecWithNoAttributes(structureClass)
			isUnion -> toUnionSpec(packageName, structureClass)
			else -> toSpecWithAttributes(packageName, structureClass)
		}
	}

private fun NativeStructure.generateFunctionPointerTypeInterface(packageName: String) = fields
	.mapNotNull { it.toFunctionPointerTypeInterface(packageName, name) }

private fun Pair<String, TypeRef>.toFunctionPointerTypeInterface(packageName: String, structureName: String) =
	let { (fieldName, typeRef) ->
		when (typeRef) {
			is ResolvedTypeRef -> {
				val rootType = typeRef.type.rootType()
				when {
					rootType is FunctionPointerType
						&& typeRef.type !is NativeTypeAlias -> rootType.toCallbackSpec(
						generateNativePointerName(structureName, fieldName),
						packageName
					)

					else -> null
				}
			}

			else -> null
		}
	}

private fun NativeStructure.toUnionSpec(packageName: String, structureClass: ClassName) =
	TypeSpec.classBuilder(structureClass)
		.addModifiers(KModifier.OPEN)
		.superclass(jnaUnion)
		.addFunction(
			FunSpec.constructorBuilder()
				.addParameter(
					ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
						.build()
				).callSuperConstructor("pointer")
				.build()
		)
		.addFunction(
			FunSpec.constructorBuilder()
				.build()
		)
		.apply {
			fields.forEach { (name, typeRef) ->
				addProperty(
					propertySpec(name, typeRef, packageName)
				)
			}
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
		.addFunction(
			FunSpec.constructorBuilder()
				.addParameter(
					ParameterSpec.builder("pointer", jnaPointer.copy(nullable = true))
						.build()
				).callSuperConstructor("pointer")
				.build()
		)
		.addFunction(
			FunSpec.constructorBuilder()
				.build()
		)
		.apply {
			fields.forEach { (name, typeRef) ->
				addProperty(
					propertySpec(name, typeRef, packageName)
				)
			}
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

private fun NativeStructure.propertySpec(
	name: String,
	typeRef: TypeRef,
	packageName: String
): PropertySpec = when (typeRef) {
	is ResolvedTypeRef -> typeRef.toPropertySpec(name, packageName, this)
	else -> typeRef.defaultPropertySpec(name)
}

private fun ResolvedTypeRef.toPropertySpec(
	name: String,
	packageName: String,
	nativeStructure: NativeStructure
): PropertySpec {

	val rootType = type.rootType()

	val type = when {
		rootType is NativeStructure -> toType(packageName)
		// If FunctionPointerType generate an interface or use the one defined by the typealias
		rootType is FunctionPointerType -> when (type) {
			is NativeTypeAlias -> ClassName(packageName, type.name)
			else -> ClassName(packageName, generateNativePointerName(nativeStructure, name))
		}.copy(nullable = true)

		rootType is StringType -> toType(packageName)
		rootType is PrimitiveType && isPointer.not() -> toType(packageName)
		rootType is NativeEnumeration && isPointer.not() -> when (rootType.type) {
			is ResolvedTypeRef -> rootType.type.toType(packageName)
			else -> null
		}

		else -> null
	} ?: return defaultPropertySpec(name)

	val defaultValue = when {
		rootType is NativeStructure -> when {
			isPointer -> "${rootType.name}()"
			else -> "${rootType.name}()"
		}
		rootType is StringType -> "\"\""
		isPointer -> "null"
		rootType is FixeSizeType -> when {
			isArray ->  when {
				rootType.isFloating && rootType.size == 32 -> "FloatArray(${arraySize ?: 0})"
				rootType.isFloating && rootType.size == 64 -> "DoubleArray(${arraySize ?: 0})"
				rootType.size == 8 -> "ByteArray(${arraySize ?: 0})"
				rootType.size == 16 -> "ShortArray(${arraySize ?: 0})"
				rootType.size == 32 -> "IntArray(${arraySize ?: 0})"
				rootType.size == 64 -> "LongArray(${arraySize ?: 0})"
				else -> "null"
			}
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
		.addKdoc("mapped from $referenceAsString")
		.addAnnotation(jnaJvmField)
		.initializer(defaultValue)
		.mutable(true)
		.build()
}

private fun generateNativePointerName(structureName: String, fieldName: String) =
	"${structureName}${fieldName.replaceFirstChar { it.uppercaseChar() }}Function"

private fun generateNativePointerName(nativeStructure: NativeStructure, fieldName: String) =
	generateNativePointerName(nativeStructure.name, fieldName)

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