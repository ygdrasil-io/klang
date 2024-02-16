package klang.mapper

import com.squareup.kotlinpoet.PropertySpec
import klang.domain.NativeConstant

internal fun NativeConstant<*>.toSpec(packageName: String) = PropertySpec.builder(name.toString(), value::class)
	.initializer(when(value) {
		is String -> "\"$value\""
		is Double -> value.toString()
		is Long -> "${value}L"
		else -> error("unsupported constant type ${value::class} on code generation")
	})
	.build()
