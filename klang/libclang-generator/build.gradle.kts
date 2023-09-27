plugins {
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("org.jetbrains.kotlinx.kover")
}

dependencies {
	implementation (project(":klang"))
	implementation("io.github.microutils:kotlin-logging:1.7.4")
	implementation("org.slf4j:slf4j-simple:1.7.26")
}