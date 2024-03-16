rootProject.name = "sdl"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libsdl", ":binaries", ":examples:snake", ":examples:tetris")
findProject(":libsdl")?.name = "sdl2-4k"
findProject(":binaries")?.name = "sdl2-binaries"


