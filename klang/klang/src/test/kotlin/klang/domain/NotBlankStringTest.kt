package klang.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class NotBlankStringTest : FreeSpec({

	"should return null on blank string" {
		listOf("", " ", "\t").forEach {
			notBlankString(it) shouldBe null
		}
	}

	"should not return null on not blank string" {
		notBlankString("a") shouldNotBe null
	}

	"should throw an error if string is blank" {
		listOf("", " ", "\t").forEach {
			shouldThrow<IllegalStateException> {
				NotBlankString(it)
			}
		}
	}

	"should not throw an error if string is not blank" {
		NotBlankString("a")
	}

})
