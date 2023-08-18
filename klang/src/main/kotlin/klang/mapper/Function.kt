package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeFunction
import klang.domain.ResolvedTypeRef
import klang.domain.TypeRef

internal fun generateInterfaceLibrarySpec(name: String, libraryName: String) = PropertySpec
	.builder(name, jnaNativeLoad)
	.initializer("by lazy { klang.internal.NativeLoad<$name>(\"$libraryName\") }")
	.build()

internal fun List<NativeFunction>.toInterfaceSpec(packageName: String, name: String) = ClassName("", name)
	.let { interfaceClass ->
		TypeSpec.interfaceBuilder(interfaceClass)
			.addSuperinterface(jnaLibrary)
			.addFunctions(map { it.toSpec(packageName) })
			.build()
	}

private fun NativeFunction.toSpec(packageName: String) = FunSpec
	.builder(name)
	.addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
	.returns(returnType.toType(packageName))
	.addParameters(arguments.map { it.toSpec(packageName) })
	.build()


private fun NativeFunction.Argument.toSpec(packageName: String) = ParameterSpec
	.builder(name ?: "", type.toType(packageName))
	.build()
