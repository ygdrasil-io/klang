package darwin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

val IS_OS_DARWIN = System.getProperty("os.name").contains("mac", ignoreCase = true)

class NSStringTest : FreeSpec({

	val stringToTest = "Lorem ipsum"

    "test string length".config(enabled = IS_OS_DARWIN) {
		NSString("stringToTest").length shouldBe (stringToTest.length + 1)
	}
})