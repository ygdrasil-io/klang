plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		binaries.executable()
		browser {
			/*commonWebpackConfig {
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {

					static = (static ?: mutableListOf()).apply {
						// Serve sources to debug inside browser
						add(project.rootDir.path)
					}
				}
			}*/

		}
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(":examples:common"))
			}
		}

	}
}

