rootProject.name = "sdl"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libsdl")
findProject(":libsdl")?.name = "sdl4k"

