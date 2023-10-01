plugins {
	kotlin("jvm")
	id("org.jetbrains.kotlinx.kover")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":libclang"))
	testImplementation(libs.kotest)
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
	forkEvery = 1
	exclude("klang/**")
}

