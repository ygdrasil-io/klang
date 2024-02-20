package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeFunction

internal fun generateInterfaceLibrarySpec(packageName: String, name: String, libraryName: String) = PropertySpec
	.builder("lib$name", ClassName(packageName, name))
	.delegate("lazy { klang.internal.NativeLoad<$packageName.$name>(\"$libraryName\") }")
	.build()

internal fun List<NativeFunction>.toInterfaceSpec(packageName: String, name: String) = ClassName("", name)
	.let { interfaceClass ->
		TypeSpec.interfaceBuilder(interfaceClass)
			.addSuperinterface(jnaLibrary)
			.addFunctions(map { it.toInterfaceFunSpec(packageName) })
			.build()
	}

internal fun List<NativeFunction>.toFunctionsSpec(packageName: String, libraryName: String) = map {
		 it.toInterfaceFunSpec(packageName) {
			 "return $libraryName.${it.name}(${it.arguments.mapIndexed { index, argument -> argument.name?.value ?: "parameter$index" }.joinToString(", ")})"
		 }
	}

private fun NativeFunction.toInterfaceFunSpec(packageName: String, bodyProvider: (() -> String)? = null) = FunSpec
	.builder(name.value)
	.addModifiers(if(bodyProvider == null) listOf(KModifier.PUBLIC, KModifier.ABSTRACT) else listOf(KModifier.PUBLIC))
	.returns(returnType.toType(packageName).copy(nullable = returnType.isPointer))
	.addParameters(arguments.mapIndexed { index, argument -> argument.toSpec(packageName, index) })
	.also { if(bodyProvider != null) it.addStatement(bodyProvider()) }
	.build()

private fun NativeFunction.Argument.toSpec(packageName: String, index: Int) = ParameterSpec
	.builder(name?.value ?: "parameter$index", type.toType(packageName).copy(nullable = type.isNullable ?: true))
	//TODO find how to escape %
	.addKdoc("mapped from ${type.referenceAsString.replace("%", "")}")
	.build()
