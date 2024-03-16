import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = versionCatalogs.named("libs")

plugins {
	`java-gradle-plugin`
	kotlin("jvm") version libs.versions.kotlin
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.pgpainless:pgpainless-sop:1.6.5")
	implementation("net.lingala.zip4j:zip4j:2.11.5")
	implementation("com.google.code.gson:gson:2.10.1")

}

configure<JavaPluginExtension> {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	sourceSets {
		getByName("main").java.srcDirs("src/main/kotlin")
	}
}