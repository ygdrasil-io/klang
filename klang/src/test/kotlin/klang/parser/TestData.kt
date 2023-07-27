package klang.parser

import klang.domain.ObjectiveCCategory
import klang.domain.ObjectiveCClass
import klang.domain.ObjectiveCProtocol
import klang.domain.UnresolvedTypeRef

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
			superType = "MyClass",
			methods = listOf(
				ObjectiveCClass.Method("newMethod", "void", true),
			)
		)
	)

	val objectiveCProtocol = listOf(
		ObjectiveCProtocol(
			name = "MyProtocol",
			protocols = setOf("NSObject"),
			properties = listOf(),
			methods = listOf(
				ObjectiveCClass.Method("method1", "void", true),
				ObjectiveCClass.Method("method2", "NSString *", true)
			)
		)
	)

	val objectiveCClass = listOf(
		ObjectiveCClass(
			name = "TestClass",
			superType = "NSObject",
			protocols = setOf(UnresolvedTypeRef("NSCopying")),
			properties = listOf(
				ObjectiveCClass.Property("testProperty", "NSString *", nonatomic = true, assign = true)
			),
			methods = listOf(
				ObjectiveCClass.Method("testMethod", "void", true),
				ObjectiveCClass.Method(
					"testMethod:withParameter:", "BOOL", false, listOf(
						ObjectiveCClass.Method.Argument("parameter", "NSString *"),
						ObjectiveCClass.Method.Argument("testParameter", "NSString *"),
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