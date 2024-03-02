@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		browser()
		nodejs()
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
