rootProject.name = "angle"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libgles", ":libangle", ":binaries")
findProject(":libangle")?.name = "angle4k"
findProject(":binaries")?.name = "angle-binaries"
include("example")
