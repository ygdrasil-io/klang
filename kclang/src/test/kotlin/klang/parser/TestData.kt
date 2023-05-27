package klang.parser

object TestData {
	val enumerations = listOf(
		"EnumName" to listOf("Value1" to 0x2L, "Value2" to 0x1L),
		"EnumNameWithoutExplicitValues" to listOf(
			"EnumNameWithoutExplicitValues_Value1" to 0L,
			"EnumNameWithoutExplicitValues_Value2" to 1L
		)
	)
}