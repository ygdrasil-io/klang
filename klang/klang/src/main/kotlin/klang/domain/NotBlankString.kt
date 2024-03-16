package klang.domain

fun notBlankString(value: String) = value.takeIf(String::isNotBlank)
	?.let { NotBlankString(value) }

@JvmInline
value class NotBlankString(val value: String) : Comparable<String> by value, CharSequence by value  {

	init {
		check(value.isNotBlank()) {
			error("value cannot be blank")
		}
	}

	override fun toString(): String {
		return value
	}

}