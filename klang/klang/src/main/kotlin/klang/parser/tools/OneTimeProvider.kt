package klang.parser.tools

class OneTimeProvider<T>(private var value: T? = null) {

	fun store(value: T) {
		this.value = value
	}

	fun consume(): T? {
		val value = this.value
		this.value = null
		return value
	}
}