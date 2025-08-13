package org.mifospay

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)
internal fun Project.configureKotlinMultiplatform() {
    // Read ONLY from Gradle project property: kmp.ios.deviceOnly
    // (e.g., provided by ~/.gradle/gradle.properties in CI or via -P on CLI)
    val deviceOnlyIos: Boolean = providers
        .gradleProperty("kmp.ios.deviceOnly")
        .orElse("false")
        .map { it.equals("true", ignoreCase = true)}
        .get()

    extensions.configure<KotlinMultiplatformExtension> {
        applyProjectHierarchyTemplate()

        jvm("desktop")
        androidTarget()
        // iOS targets – controlled by the Gradle property
        if (deviceOnlyIos) {
            iosArm64()
        } else {
            iosArm64()
            iosSimulatorArm64()
            iosX64()
        }
        js(IR) {
            this.nodejs()
            binaries.executable()
        }
        wasmJs() {
            browser()
            nodejs()
        }

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
}