
plugins {
	kotlin("jvm") version libs.versions.kotlin
	id("com.gradle.plugin-publish") version "1.0.0"
}

repositories {
	mavenCentral()
}

group = "io.ygdrasil"
version = "1.0.0-SNAPSHOT"

subprojects {
	apply(plugin = "maven-publish")
	apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

	group = "io.ygdrasil"
	version = "1.0.0-SNAPSHOT"

	publishing {

		publications {
			create<MavenPublication>("maven") {
				from(components["java"])

				pom {
					name = "Klang-${project.name}"
					description = "Angle binding"
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
}


