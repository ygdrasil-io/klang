rootProject.name = "angle"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libgles")
include(":libangle")
findProject(":libangle")?.name = "angle4k"
include("example")
