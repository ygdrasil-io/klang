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
			.addFunctions(map { it.toSpec(packageName) })
			.build()
	}

private fun NativeFunction.toSpec(packageName: String) = FunSpec
	.builder(name.value)
	.addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
	.returns(returnType.toType(packageName))
	.addParameters(arguments.mapIndexed { index, argument -> argument.toSpec(packageName, index) })
	.build()

private fun NativeFunction.Argument.toSpec(packageName: String, index: Int) = ParameterSpec
	.builder(name?.value ?: "parameter$index", type.toType(packageName).copy(nullable = type.isNullable ?: true))
	//TODO find how to escape %
	.addKdoc("mapped from ${type.referenceAsString.replace("%", "")}")
	.build()
