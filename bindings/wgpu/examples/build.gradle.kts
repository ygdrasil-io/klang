@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		binaries.executable()
		browser {
			commonWebpackConfig {
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					// Uncomment and configure this if you want to open a browser different from the system default
					// open = mapOf(
					//     "app" to mapOf(
					//         "name" to "google chrome"
					//     )
					// )

					static = (static ?: mutableListOf()).apply {
						// Serve sources to debug inside browser
						add(project.rootDir.path)
					}
				}
			}

			// Uncomment the next line to apply Binaryen and get optimized wasm binaries
			// applyBinaryen()
		}
	}
	//targetHierarchy.default()
	//jvm()
	/*androidTarget {
		publishLibraryVariants("release")
		compilations.all {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}
	iosX64()
	iosArm64()
	iosSimulatorArm64()
	linuxX64()*/

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(project(":wgpu4k"))
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
				//put your multiplatform dependencies here
			}
		}
		val commonTest by getting {
			dependencies {
				//implementation(libs.bundles.kotest)
			}
		}
	}
}

/*android {
    namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}*/
