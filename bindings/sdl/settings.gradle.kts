rootProject.name = "sdl"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libsdl", ":examples:snake")
findProject(":libsdl")?.name = "sdl4k"

