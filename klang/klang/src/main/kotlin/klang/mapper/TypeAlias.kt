package klang.mapper

import com.squareup.kotlinpoet.*
import klang.domain.NativeTypeAlias


internal fun NativeTypeAlias.toSpec(packageName: String) = when {
	type.isCallback -> toCallbackSpec(packageName)
	else -> toTypeAliasSpec(packageName)
}

private fun NativeTypeAlias.toTypeAliasSpec(packageName: String) = TypeAliasSpec
	.builder(name, type.toType(packageName))
	.build()

private fun NativeTypeAlias.toCallbackSpec(packageName: String) = TypeSpec.interfaceBuilder(name)
	.addSuperinterface(jnaCallback)
	.addFunction(
		FunSpec.builder("invoke")
			.addModifiers(KModifier.OPERATOR)
			.addModifiers(KModifier.ABSTRACT)
			.build()
	)
	.build()