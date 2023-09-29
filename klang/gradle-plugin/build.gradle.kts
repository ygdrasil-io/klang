plugins {
	id("com.gradle.plugin-publish") version "1.0.0"
	id("maven-publish")
	kotlin("jvm") version "1.9.0"
}

gradlePlugin {
	plugins {
		create("klang") {
			id = "io.ygdrasil.klang"
			implementationClass = "io.ygdrasil.KlangPlugin"
		}
	}
}

group = "io.ygdrasil"
version = "1.0.0-SNAPSHOT"


dependencies {
	api(project(":klang"))
	api(project(":docker-toolkit"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(libs.kotest)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {

	publishing {
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
