plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
}

val projectVersion = System.getenv("VERSION")
	?.takeIf { it.isNotBlank() }
	?: "0.0.0"

allprojects {
	apply(plugin = "maven-publish")
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
	apply(plugin = "org.jetbrains.kotlinx.kover")

	repositories {
		mavenCentral()
	}

	group = "io.ygdrasil"
	version = projectVersion

	kotlin {
		jvmToolchain(17)

    sourceSets.all {
        languageSettings {
            java {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
            languageVersion = "2.0"
        }

	publishing {

		publications {
			create<MavenPublication>("maven") {
				from(components["java"])
			}
		}

		repositories {
			maven {
				name = "GitLab"
				url = uri(System.getenv("URL") ?: "")
				credentials(HttpHeaderCredentials::class) {
					name = "Deploy-Token"
					value = System.getenv("TOKEN")
				}
				authentication {
					create<HttpHeaderAuthentication>("header")
				}
			}
		}
	}
}


