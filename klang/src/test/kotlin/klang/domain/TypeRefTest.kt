package klang.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TypeRefTest : FreeSpec({

	"test void type characteristic" {
		typeOf("void").apply {
			isLeft() shouldBe false
			onRight {
				it.apply {
					isConstant shouldBe false
					isPointer shouldBe false
					isStructure shouldBe false
					isEnumeration shouldBe false
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
					typeName shouldBe "unsigned int"
				}
			}
		}
	}

})
