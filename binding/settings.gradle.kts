rootProject.name = "klang-binding"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}

	/*resolutionStrategy {
		eachPlugin {
			if (requested.id.namespace == "io.ygdrasil.klang") {
				useModule("io.ygdrasil:klang-gradle-plugin:${requested.version}")
			}
		}
	}*/
}

include(":sdl")
findProject(":sdl")?.name = "sdl4k"

