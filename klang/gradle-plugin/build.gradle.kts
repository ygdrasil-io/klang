plugins {
	id("com.gradle.plugin-publish") version "1.0.0"
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