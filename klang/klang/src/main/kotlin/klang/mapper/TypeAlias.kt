package klang.mapper

import com.squareup.kotlinpoet.TypeAliasSpec
import klang.domain.NativeTypeAlias

internal fun NativeTypeAlias.toSpec(packageName: String) = TypeAliasSpec
	.builder(name, type.toType(packageName))
	.build()