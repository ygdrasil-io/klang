
pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
	}
}

include(":libwgpu", ":binaries")
findProject(":libwgpu")?.name = "wgpu4k"
findProject(":binaries")?.name = "wgpu-binaries"
include(":example")

