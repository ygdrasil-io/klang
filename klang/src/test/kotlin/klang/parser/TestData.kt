package klang.parser

import klang.domain.ObjectiveCClass

object TestData {

	val objectiveCClass = listOf(
		"TestClass" to listOf(
			ObjectiveCClass.Property("testProperty", "NSString *", nonatomic = true, assign = true),
			ObjectiveCClass.Method("testMethod", "void", true),
			ObjectiveCClass.Method("testMethod:withParameter:", "BOOL", false, listOf(
				ObjectiveCClass.Method.Argument("parameter", "NSString *"),
				ObjectiveCClass.Method.Argument("testParameter", "NSString *"),
			)),
		)
	)

	val enumerations = listOf(
		"EnumName" to listOf("Value1" to 0x2L, "Value2" to 0x1L),
		"EnumNameWithoutExplicitValues" to listOf(
			"EnumNameWithoutExplicitValues_Value1" to 0L,
			"EnumNameWithoutExplicitValues_Value2" to 1L
		)
	)

	val structures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "struct StructName", "field2" to "struct StructName *", "field3" to "char")
	)

	val typeDefStructures = listOf(
		"StructName" to listOf("field1" to "enum EnumName *", "field2" to "EnumName2", "field3" to "char"),
		"StructName2" to listOf("field1" to "StructName", "field2" to "StructName *", "field3" to "char")
	)

	val typeDef = listOf(
		"NewType" to "void *",
		"NewStructureType" to "struct OldStructureType *"
	)
}