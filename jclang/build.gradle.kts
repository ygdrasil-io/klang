plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    jvmToolchain(20)

    sourceSets.all {
        languageSettings {
            java {
                sourceCompatibility = JavaVersion.VERSION_20
                targetCompatibility = JavaVersion.VERSION_20
            }
            languageVersion = "2.0"
        }
    }
}

dependencies {
    api ("net.java.dev.jna:jna:5.13.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.github.microutils:kotlin-logging:1.7.4")
    implementation ("org.slf4j:slf4j-simple:1.7.26")
	testImplementation(libs.kotest)
}

tasks.test {
    useJUnitPlatform()
}
