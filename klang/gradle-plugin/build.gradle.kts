plugins {
	kotlin("jvm")
	id("com.gradle.plugin-publish")
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

