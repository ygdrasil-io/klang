rootProject.name = "wgpu"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libwgpu")
findProject(":libwgpu")?.name = "wgpu4k"

