package io.ygdrasil

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.gradle.testfixtures.ProjectBuilder


class KlangPluginTest : FreeSpec({

	"greeterPluginAddsGreetingTaskToProject" {
		val project = ProjectBuilder.builder().build();
		project.pluginManager.apply("io.ygdrasil.klang");

		project.tasks.getByName("hello20") shouldNotBe  null
	}
})
