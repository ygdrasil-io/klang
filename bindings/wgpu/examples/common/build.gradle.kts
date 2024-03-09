@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		browser()
	}
	jvm()


	sourceSets {
		val commonMain by getting {
			dependencies {
				api(project(":wgpu4k"))
				api(libs.coroutines)
				implementation("com.soywiz.korge:korlibs-math-core:5.4.0")

			}
		}
	}
}