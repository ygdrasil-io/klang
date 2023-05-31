plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
	implementation("com.github.docker-java:docker-java-core:3.3.1")
	implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.1")
	implementation("io.github.microutils:kotlin-logging:1.7.4")
	implementation("org.slf4j:slf4j-simple:1.7.26")
}
