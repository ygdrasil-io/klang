package io.ygdrasil

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import org.gradle.testfixtures.ProjectBuilder

class KlangPluginTest : FreeSpec({

	"klang gradle plugin add generateBinding task" {
		val project = ProjectBuilder.builder().build();
		project.pluginManager.apply("io.ygdrasil.klang");

		project.tasks.getByName("generateBinding") shouldNotBe  null
	}
})
