package klang.domain

import kotlin.reflect.KClass

val acceptableConstantType = listOf<KClass<*>>(String::class, Double::class, Long::class)

data class NativeConstant<T: Any>(
	override val name: NotBlankString,
	val value: T,
	override val source: DeclarationOrigin = DeclarationOrigin.Unknown
) : NameableDeclaration {

	init {
		check(value::class in acceptableConstantType) { "${value::class} not supported on constant type"}
	}
}