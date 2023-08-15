rootProject.name = "klang-binding"

pluginManagement {
	repositories {
		mavenLocal()
		gradlePluginPortal()
		mavenCentral()
	}

	resolutionStrategy {
		eachPlugin {
			if (requested.id.namespace == "io.ygdrasil.klang") {
				useModule("io.ygdrasil:klang-gradle-plugin:${requested.version}")
			}
		}
	}
}

include(":sdl")
findProject(":sdl")?.name = "sdl4k"

