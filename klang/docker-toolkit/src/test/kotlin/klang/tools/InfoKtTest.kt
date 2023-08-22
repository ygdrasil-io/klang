package klang.tools

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class InfoKtTest : FreeSpec({

	"docker should be running" {
		dockerIsRunning() shouldBe true
	}
})
