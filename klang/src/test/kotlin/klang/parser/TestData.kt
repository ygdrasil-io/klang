package klang.parser

import klang.domain.*

fun testType(name: String) = typeOf(name).getOrNull() ?: error("fail to create type $name")

object TestData {

	val objectiveCEnumeration = listOf(
		"MyEnum" to listOf(
			"kValue1" to 0L,
			"kValue2" to 1L,
			"kValue3" to 2L
		),
		"MyEnum2" to listOf(
			"kValue4" to 0L,
			"kValue5" to 1L,
			"kValue6" to 2L
		)
	)

	val objectiveCCategory = listOf(
		ObjectiveCCategory(
			name = "MyCategory",
			superType = testType("MyClass"),
			methods = listOf(
				ObjectiveCClass.Method("newMethod", testType("void"), true),
			)
		)
	)

	val objectiveCProtocol = listOf(
		ObjectiveCProtocol(
			name = "MyProtocol",
			protocols = setOf("NSObject"),
			properties = listOf(),
			methods = listOf(
				ObjectiveCClass.Method("method1", testType("void"), true),
				ObjectiveCClass.Method("method2", testType("NSString *"), true)
			)
		)
	)

	val objectiveCClass = listOf(
		ObjectiveCClass(
			name = "TestClass",
			superType = testType("NSObject"),
			protocols = setOf(testType("NSCopying")),
			properties = listOf(
				ObjectiveCClass.Property("testProperty", "NSString *", nonatomic = true, assign = true, unsafe_unretained = true, readwrite = true)
			),
			methods = listOf(
				ObjectiveCClass.Method("testMethod", testType("void"), true),
				ObjectiveCClass.Method(
					"testMethod:withParameter:", testType("BOOL"), false, listOf(
						ObjectiveCClass.Method.Argument("parameter", testType("NSString *")),
						ObjectiveCClass.Method.Argument("testParameter", testType("NSString *")),
					)
				)
			)
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