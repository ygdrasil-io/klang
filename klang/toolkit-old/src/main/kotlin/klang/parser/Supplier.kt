package klang.parser


fun interface Supplier<T> {
    fun get(): T
}