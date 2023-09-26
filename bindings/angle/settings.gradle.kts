rootProject.name = "angle"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libangle")
findProject(":libangle")?.name = "angle4k"

