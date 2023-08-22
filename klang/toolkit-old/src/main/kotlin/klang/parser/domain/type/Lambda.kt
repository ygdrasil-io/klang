package klang.parser.domain.type

class Lambda(
    val varargs: Boolean,
    val argumentTypeds: List<Typed>,
    val returnTyped: Typed,
    val paramNames: List<String> = listOf()
) : Typed() {


    fun withParameterNames(paramNames: List<String>): Lambda {
        return Lambda(varargs, argumentTypeds, returnTyped, paramNames)
    }
}