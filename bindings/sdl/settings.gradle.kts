rootProject.name = "sdl"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libsdl", ":examples:snake", ":examples:tetris")
findProject(":libsdl")?.name = "sdl4k"

