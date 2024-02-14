package klang.parser

import klang.domain.*

fun testType(name: String) = testType(NotBlankString(name))

fun testType(name: NotBlankString) = typeOf(name.value).let {
	it.unchecked("fail to create type $name, cause: ${it.leftOrNull()}")
}

object TestData {

	val functions = listOf(
		NativeFunction(
			name = NotBlankString("function"),
			returnType = testType("char"),
			arguments = listOf(
				NativeFunction.Argument(NotBlankString("a"), testType("int *")),
				NativeFunction.Argument(NotBlankString("b"), testType("void *")),
				NativeFunction.Argument(NotBlankString("myEnum"), testType("enum EnumName")),
			)
		),
		NativeFunction(
			name = NotBlankString("function2"),
			returnType = testType("void *"),
			arguments = listOf()
		),

		NativeFunction(
			name = NotBlankString("function3"),
			returnType = testType("struct StructName *"),
			arguments = listOf()
		)
	)

	val objectiveCEnumeration = listOf(
		NotBlankString("MyEnum") to listOf(
			"kValue1" to 0L,
			"kValue2" to 1L,
			"kValue3" to 2L
		),
		NotBlankString("MyEnum2") to listOf(
			"kValue4" to 0L,
			"kValue5" to 1L,
			"kValue6" to 2L
		)
	)

	val objectiveCCategory = listOf(
		ObjectiveCCategory(
			name = NotBlankString("MyCategory"),
			superType = testType("MyClass"),
			methods = listOf(
				ObjectiveCClass.Method(NotBlankString("newMethod"), testType("void"), true),
			)
		)
	)

	val objectiveCProtocol = listOf(
		ObjectiveCProtocol(
			name = NotBlankString("MyProtocol"),
			protocols = setOf("NSObject"),
			properties = listOf(),
			methods = listOf(
				ObjectiveCClass.Method(NotBlankString("method1"), testType("void"), true),
				ObjectiveCClass.Method(NotBlankString("method2"), testType("NSString *"), true)
			)
		)
	)

	val objectiveCClass = listOf(
		ObjectiveCClass(
			name = NotBlankString("TestClass"),
			superType = testType("NSObject"),
			protocols = setOf(testType("NSCopying")),
			properties = listOf(
				ObjectiveCClass.Property(
					NotBlankString("testProperty"),
					"NSString *",
					nonatomic = true,
					assign = true,
					unsafe_unretained = true,
					readwrite = true
				)
			),
			methods = listOf(
				ObjectiveCClass.Method(NotBlankString("testMethod"), testType("void"), true),
				ObjectiveCClass.Method(
					NotBlankString("testMethod:withParameter:"), testType("BOOL"), false, listOf(
						ObjectiveCClass.Method.Argument("parameter", testType("NSString *")),
						ObjectiveCClass.Method.Argument("testParameter", testType("NSString *")),
					)
				)
			)
		)
	)

	val enumerations = listOf(
		NotBlankString("EnumName") to listOf("Value1" to 0x2L, "Value2" to 0x1L),
		NotBlankString("EnumNameWithoutExplicitValues") to listOf(
			"EnumNameWithoutExplicitValues_Value1" to 0L,
			"EnumNameWithoutExplicitValues_Value2" to 1L
		)
	)

	val union = listOf(
		NativeStructure(
			name = NotBlankString("MyUnion"),
			fields = listOf(
				TypeRefField("i", testType("int")),
				TypeRefField("f", testType("float")),
				TypeRefField("c", testType("char")),

				),
			isUnion = true
		)
	)


	val structures = listOf(
		NativeStructure(
			name = NotBlankString("StructName"),
			fields = listOf(
				TypeRefField("field1", testType("enum EnumName *")),
				TypeRefField("field2", testType("EnumName2")),
				TypeRefField("field3", testType("char"))
			)
		),
		NativeStructure(
			name = NotBlankString("StructName2"),
			fields = listOf(
				TypeRefField("field1", testType("struct StructName")),
				TypeRefField("field2", testType("struct StructName *")),
				TypeRefField("field3", testType("char"))
			)
		)
	)

	val typeDefStructures = listOf(

		NativeStructure(
			name = NotBlankString("StructName"),
			fields = listOf(
				TypeRefField("field1", testType("enum EnumName *")),
				TypeRefField("field2", testType("EnumName2")),
				TypeRefField("field3", testType("char"))
			)
		),
		NativeStructure(
			name = NotBlankString("StructName2"),
			fields = listOf(
				TypeRefField("field1", testType("StructName")),
					TypeRefField("field2", testType("StructName *")),
						TypeRefField("field3", testType("char"))
			)
		)
	)

	val typeDef = listOf(
		NativeTypeAlias(
			name = NotBlankString("NewType"),
			typeRef = testType("void *")
		),
		NativeTypeAlias(
			name = NotBlankString("NewStructureType"),
			typeRef = testType("struct OldStructureType *")
		)
	)

	val basicFunctionPointer = NotBlankString("void (*)(void *, char *, int)")

	val exaustiveTypeDef = listOf(
		NativeTypeAlias(
			name = NotBlankString("signed_char_t"),
			typeRef = testType("char")
		),
		NativeTypeAlias(
			name = NotBlankString("signed_int_t"),
			typeRef = testType("int")
		),
		NativeTypeAlias(
			name = NotBlankString("signed_short_t"),
			typeRef = testType("short")
		),
		NativeTypeAlias(
			name = NotBlankString("signed_long_t"),
			typeRef = testType("long")
		),
		NativeTypeAlias(
			name = NotBlankString("signed_long_long_t"),
			typeRef = testType("long long")
		),
		NativeTypeAlias(
			name = NotBlankString("unsigned_char_t"),
			typeRef = testType("unsigned char")
		),
		NativeTypeAlias(
			name = NotBlankString("unsigned_int_t"),
			typeRef = testType("unsigned int")
		),
		NativeTypeAlias(
			name = NotBlankString("unsigned_short_t"),
			typeRef = testType("unsigned short")
		),
		NativeTypeAlias(
			name = NotBlankString("unsigned_long_t"),
			typeRef = testType("unsigned long")
		),
		NativeTypeAlias(
			name = NotBlankString("unsigned_long_long_t"),
			typeRef = testType("unsigned long long")
		),
		NativeTypeAlias(
			name = NotBlankString("arr_of_int_t"),
			typeRef = testType("int[10]")
		),
		NativeTypeAlias(
			name = NotBlankString("arr_of_float_t"),
			typeRef = testType("float[10]")
		),
		NativeTypeAlias(
			name = NotBlankString("arr_of_char_t"),
			typeRef = testType("char[10]")
		),
		NativeTypeAlias(
			name = NotBlankString("arr_of_double_t"),
			typeRef = testType("double[10]")
		),
		NativeTypeAlias(
			name = NotBlankString("arr_of_unsigned_char_t"),
			typeRef = testType("char[10]")
		)
	)
}