
plugins {
    kotlin("jvm")
	id("org.jetbrains.kotlinx.kover")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation (project(":libclang"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.6.1")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    forkEvery = 1
	exclude("klang/**")
}

