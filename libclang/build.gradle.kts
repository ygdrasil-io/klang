import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    api ("net.java.dev.jna:jna:5.13.0")
}
