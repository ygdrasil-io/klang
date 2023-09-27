package klang.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import klang.parser.TestData

class TypeRefTest : FreeSpec({

	"test FunctionPointerType parsing" {
		TestData.basicFunctionPointer.toFunctionPointerType().apply {
			returnType.apply {
				typeName shouldBe "void"
				isPointer shouldBe false
			}
			arguments.size shouldBe 3
			arguments[0].apply {
				typeName shouldBe "void"
				isPointer shouldBe true
			}
			arguments[1].apply {
				typeName shouldBe "char"
				isPointer shouldBe true
			}
			arguments[2].apply {
				typeName shouldBe "int"
				isPointer shouldBe false
			}
		}
	}

	"test void type characteristic" {
		typeOf("void").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe false
					isPointer shouldBe false
					isStructure shouldBe false
					isEnumeration shouldBe false
					isNullable shouldBe null
					typeName shouldBe "void"
				}
			}
		}
	}

	"test int * type characteristic" {
		typeOf("int *").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe false
					isPointer shouldBe true
					isStructure shouldBe false
					isEnumeration shouldBe false
					isNullable shouldBe null
					typeName shouldBe "int"
				}
			}
		}
	}


	"test const int * type characteristic" {
		typeOf("const int *").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe true
					isPointer shouldBe true
					isStructure shouldBe false
					isEnumeration shouldBe false
					isNullable shouldBe null
					typeName shouldBe "int"
				}
			}
		}
	}

	"test const unsigned int * type characteristic" {
		typeOf("const unsigned int *").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe true
					isPointer shouldBe true
					isStructure shouldBe false
					isEnumeration shouldBe false
					isNullable shouldBe null
					typeName shouldBe "unsigned int"
				}
			}
		}
	}

	"test const struct AnyStruct * type characteristic" {
		typeOf("const struct AnyStruct *").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe true
					isPointer shouldBe true
					isStructure shouldBe true
					isEnumeration shouldBe false
					isNullable shouldBe null
					typeName shouldBe "AnyStruct"
				}
			}
		}
	}

	"test const enum AnyEnum * type characteristic" {
		typeOf("const enum AnyEnum *").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe true
					isPointer shouldBe true
					isStructure shouldBe false
					isEnumeration shouldBe true
					isNullable shouldBe null
					typeName shouldBe "AnyEnum"
				}
			}
		}
	}
})
