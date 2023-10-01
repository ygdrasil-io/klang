plugins {
	id("com.gradle.plugin-publish") version "1.0.0"
}

gradlePlugin {
	plugins {
		create("klang-plugin") {
			id = "io.ygdrasil.klang-plugin"
			implementationClass = "io.ygdrasil.KlangPlugin"
		}
	}
}

dependencies {
	api(project(":klang"))
	api(project(":docker-toolkit"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation(libs.kotest)
}

tasks.test {
	useJUnitPlatform()
}

