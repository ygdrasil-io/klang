package klang.jvm

class IndexException(val exitCode: Int) : RuntimeException() {

    override val message: String
        get() = "Indexing failed with exit code $exitCode"
}