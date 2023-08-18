package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeFunction
import klang.domain.TypeRef

internal fun generateInterfaceLibrarySpec(name: String, libraryName: String) = PropertySpec
	.builder(name, jnaNativeLoad)
	.initializer("by lazy { darwin.internal.NativeLoad<$name>(\"$libraryName\") }")
	.build()

internal fun List<NativeFunction>.toInterfaceSpec(name: String) = ClassName("", name)
	.let { interfaceClass ->
		TypeSpec.interfaceBuilder(interfaceClass)
			.addFunctions(map { it.toSpec() })
			.build()
	}

private fun NativeFunction.toSpec() = FunSpec
	.builder(name)
	.addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
	.returns(returnType.toType())
	.addParameters(arguments.map { it.toSpec() })
	.build()


private fun NativeFunction.Argument.toSpec() = ParameterSpec
	.builder(name ?: "", type.toType())
	.build()

private fun TypeRef.toType(): ClassName = when {
	isPointer -> jnaPointer
	else -> ClassName("", typeName)
}
