import klang.publish.SonatypeCentralUploadTask

plugins {
	kotlin("jvm") version libs.versions.kotlin
	kotlin("plugin.serialization") version libs.versions.kotlin
	id("org.jetbrains.kotlinx.kover") version "0.7.3"
	id("com.gradle.plugin-publish") version "1.0.0"
}

val rootProject = project

val projectVersion = System.getenv("VERSION")
	?.takeIf { it.isNotBlank() }
	?: "0.0.0"

allprojects {
	apply(plugin = "maven-publish")
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

	repositories {
		mavenCentral()
	}

	group = "io.ygdrasil"
	version = projectVersion

	java {
		withJavadocJar()
		withSourcesJar()
	}

	kotlin {
		jvmToolchain(21)

		sourceSets.all {
			languageSettings {
				java {
					sourceCompatibility = JavaVersion.VERSION_21
					targetCompatibility = JavaVersion.VERSION_21
				}
				languageVersion = "2.0"
			}
		}
	}

	publishing {

		publications {
			create<MavenPublication>("maven") {
				from(components["java"])

				pom {
					name = "Klang-${project.name}"
					description = "Module of Klang project"
					url = "https://ygdrasil.io/"
					licenses {
						license {
							name = "MIT"
							url = "https://opensource.org/license/mit/"
						}
					}
					developers {
						developer {
							id = "alexandremo"
							name = "Alexandre Mommers"
							email = "alexandre dot mommers at gmail do com"
						}
					}
					scm {
						connection = "scm:git:git://github.com/ygdrasil-io/klang.git"
						developerConnection = "scm:git:ssh//git@github.com:ygdrasil-io/klang.git"
						url = "https://github.com/ygdrasil-io/klang"
					}
				}
			}
		}
	}

	val buildDir = project.layout.buildDirectory.locationOnly.get().asFile
	val artifact = buildDir.resolve("libs").resolve("$name-$version.jar")
	val sources = buildDir.resolve("libs").resolve("$name-$version-sources.jar")
	val javadoc = buildDir.resolve("libs").resolve("$name-$version-javadoc.jar")

	val sonatypeCentralUploadTask = tasks.register<SonatypeCentralUploadTask>("sonatypeCentralUpload-$name") {

		username = System.getenv("SONATYPE_LOGIN")
		password = System.getenv("SONATYPE_PASSWORD")
		signingKey = System.getenv("PGP_PRIVATE")
		signingKeyPassphrase = System.getenv("PGP_PASSPHRASE")
		publicKey = System.getenv("PGP_PUBLIC")

		archives = files(artifact, sources, javadoc)
		pom = file(buildDir.resolve("publications").resolve("maven").resolve("pom-default.xml"))

	}.get()

	val publishAllTask = tasks.register("publishAll") {}.get()

	if (project != rootProject) {
		tasks.filterIsInstance<GenerateMavenPom>().forEach { task -> sonatypeCentralUploadTask.dependsOn(task) }
		sonatypeCentralUploadTask.dependsOn(tasks.getByName("assemble"))
		publishAllTask.dependsOn(sonatypeCentralUploadTask)
	}
}


