rootProject.name = "wgpu"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		mavenLocal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}
plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include("wgpu4k")
//include("examples")
include("examples:common")
include("examples:compose")
