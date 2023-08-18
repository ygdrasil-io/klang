rootProject.name = "klang-binding"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":sdl")
findProject(":sdl")?.name = "sdl4k"

