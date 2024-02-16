package klang.parser.libclang

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import klang.parser.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class CConstantParserTest : ParserTestCommon({

	"test parsing of string constants" - {
		// Given
		createHeader("string-constants.h") {
			"""
				#define STRING_CONSTANT1 "Hello"
				#define STRING_CONSTANT2 "World"
				#define STRING_CONSTANT3 "AI"
				#define STRING_CONSTANT4 "Programming"
				#define STRING_CONSTANT5 "Assistant"
			""".trimIndent()
			// When
		}.parseIt {

			//Then
			validateConstants(this, TestData.stringConstants)
		}
	}

	"test parsing of long constants" - {
		// Given
		createHeader("long-constants.h") {
			"""
				#define LONG_CONSTANT1 10000000000L
				#define LONG_CONSTANT2 20000000000L
				#define LONG_CONSTANT3 30000000000L
				#define LONG_CONSTANT4 40000000000L
				#define LONG_CONSTANT5 50000000000L
			""".trimIndent()
			// When
		}.parseIt {

			//Then
			validateConstants(this, TestData.longConstants)
		}
	}

	"test parsing of double constants" - {
		// Given
		createHeader("double-constants.h") {
			"""
				#define DOUBLE_CONSTANT1 1.11
				#define DOUBLE_CONSTANT2 2.22
				#define DOUBLE_CONSTANT3 3.33
				#define DOUBLE_CONSTANT4 4.44
				#define DOUBLE_CONSTANT5 5.55
			""".trimIndent()
			// When
		}.parseIt {

			//Then
			validateConstants(this, TestData.doubleConstants)
		}
	}

})


