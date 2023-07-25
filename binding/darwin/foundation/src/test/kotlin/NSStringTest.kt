package darwin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class NSStringTest : FreeSpec({

	val stringToTest = "Lorem ipsum"

    "test string length" {
		NSString("stringToTest").length shouldBe (stringToTest.length + 1)
	}
})