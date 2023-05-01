plugins {
    id("java")
    kotlin("jvm") version "1.8.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation ("net.java.dev.jna:jna:5.13.0")
    implementation ("org.jetbrains:annotations:24.0.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.6.1")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    forkEvery = 1
}