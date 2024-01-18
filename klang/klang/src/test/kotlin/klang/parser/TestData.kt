package klang.parser

import klang.domain.*

fun testType(name: String) = typeOf(name).let {
	it.unchecked("fail to create type $name, cause: ${it.leftOrNull()}")
}

object TestData {

	val functions = listOf(
		NativeFunction(
			name = "function",
			returnType = testType("char"),
			arguments = listOf(
				NativeFunction.Argument("a", testType("int *")),
				NativeFunction.Argument("b", testType("void *")),
				NativeFunction.Argument("myEnum", testType("enum EnumName")),
			)
		),
		NativeFunction(
			name = "function2",
			returnType = testType("void *"),
			arguments = listOf()
		),

		NativeFunction(
			name = "function3",
			returnType = testType("struct StructName *"),
			arguments = listOf()
		)
	)

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
				ObjectiveCClass.Property(
					"testProperty",
					"NSString *",
					nonatomic = true,
					assign = true,
					unsafe_unretained = true,
					readwrite = true
				)
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

	val union = listOf(
		NativeStructure(
			name = "MyUnion",
			fields = listOf(
				"i" to testType("int"),
				"f" to testType("float"),
				"c" to testType("char"),

				),
			isUnion = true
		)
	)


	val structures = listOf(
		NativeStructure(
			name = "StructName",
			fields = listOf(
				"field1" to testType("enum EnumName *"),
				"field2" to testType("EnumName2"),
				"field3" to testType("char")
			)
		),
		NativeStructure(
			name = "StructName2",
			fields = listOf(
				"field1" to testType("struct StructName"),
				"field2" to testType("struct StructName *"),
				"field3" to testType("char")
			)
		)
	)

	val typeDefStructures = listOf(

		NativeStructure(
			name = "StructName",
			fields = listOf(
				"field1" to testType("enum EnumName *"),
				"field2" to testType("EnumName2"),
				"field3" to testType("char")
			)
		),
		NativeStructure(
			name = "StructName2",
			fields = listOf(
				"field1" to testType("StructName"),
				"field2" to testType("StructName *"),
				"field3" to testType("char")
			)
		)
	)

	val typeDef = listOf(
		NativeTypeAlias(
			name = "NewType",
			typeRef = testType("void *")
		),
		NativeTypeAlias(
			name = "NewStructureType",
			typeRef = testType("struct OldStructureType *")
		)
	)

	const val basicFunctionPointer = "void (*)(void *, char *, int)"


	val exaustiveTypeDef = listOf(
		NativeTypeAlias(
			name = "signed_char_t",
			typeRef = testType("char")
		),
		NativeTypeAlias(
			name = "signed_int_t",
			typeRef = testType("int")
		),
		NativeTypeAlias(
			name = "signed_short_t",
			typeRef = testType("short")
		),
		NativeTypeAlias(
			name = "signed_long_t",
			typeRef = testType("long")
		),
		NativeTypeAlias(
			name = "signed_long_long_t",
			typeRef = testType("long long")
		),
		NativeTypeAlias(
			name = "unsigned_char_t",
			typeRef = testType("unsigned char")
		),
		NativeTypeAlias(
			name = "unsigned_int_t",
			typeRef = testType("unsigned int")
		),
		NativeTypeAlias(
			name = "unsigned_short_t",
			typeRef = testType("unsigned short")
		),
		NativeTypeAlias(
			name = "unsigned_long_t",
			typeRef = testType("unsigned long")
		),
		NativeTypeAlias(
			name = "unsigned_long_long_t",
			typeRef = testType("unsigned long long")
		),
		NativeTypeAlias(
			name = "arr_of_int_t",
			typeRef = testType("int[10]")
		),
		NativeTypeAlias(
			name = "arr_of_float_t",
			typeRef = testType("float[10]")
		),
		NativeTypeAlias(
			name = "arr_of_char_t",
			typeRef = testType("char[10]")
		),
		NativeTypeAlias(
			name = "arr_of_double_t",
			typeRef = testType("double[10]")
		),
		NativeTypeAlias(
			name = "arr_of_unsigned_char_t",
			typeRef = testType("char[10]")
		)
	)
}