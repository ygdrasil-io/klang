package klang.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class KotlinClassKtTest : FreeSpec({

	validClassNames.forEach { className ->
		"test $className is a valid class name" {
			className.isValidClassName() shouldBe true
		}
	}

	invalidClassNames.forEach { className ->
		"test $className is an invalid class name" {
			className.isValidClassName() shouldBe false
		}
	}
})


val validClassNames = listOf(
	"MyClass",
	"AnotherClass",
	"ClassName",
	"ValidName",
	"TestClass",
	"ExampleClass",
	"DataModel",
	"UtilClass",
	"Helper",
	"Parser",
	"Manager",
	"Controller",
	"Service",
	"Repository",
	"Factory",
	"Builder",
	"Handler",
	"Processor",
	"Listener",
	"Observer"
)

val invalidClassNames = arrayOf(
	"invalidName!",
	"Invalid-Class",
	"123ClassName",
	"Bad@Class",
	"Incorrect Name",
	"NoSpace Allowed",
	"class.name",
	"another%Class",
	"missing-braces]",
	"Invalid?Name",
	"!WrongName",
	"This is invalid",
	"invalid+name",
	"test(name)",
	"class#name",
	"forbidden\$name",
	"noSpaceHere!",
	"Bad_Name&",
	"1testClass",
	"invalid=Class"
)



