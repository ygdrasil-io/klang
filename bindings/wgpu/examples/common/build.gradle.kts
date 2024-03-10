@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		binaries.executable()
		browser()
		generateTypeScriptDefinitions()
	}
	jvm()


	sourceSets {
		val commonMain by getting {
			dependencies {
				api(project(":wgpu4k"))
				api(libs.coroutines)
				api("com.soywiz.korge:korge-foundation:5.4.0")

			}
		}
	}
}