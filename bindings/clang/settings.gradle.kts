rootProject.name = "clang"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libclang")
findProject(":libclang")?.name = "clang4k"

