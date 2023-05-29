plugins {
	// Apply the Java Gradle Plugin Development plugin
	`java-gradle-plugin`

	// Apply the Kotlin DSL Plugin for enhanced IDE support
	`kotlin-dsl`

}

repositories {
	mavenCentral()
}

dependencies {
	// Align versions of all Kotlin components
	implementation(platform(kotlin("bom")))

	// Use the Kotlin JDK standard library
	implementation(kotlin("stdlib"))

}
