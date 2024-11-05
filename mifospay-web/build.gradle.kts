import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    js(IR) {
        moduleName = "mifospay-web"
        browser {
            commonWebpackConfig {
                outputFileName = "mifospay-web.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "mifospay-wasm"
        browser {
            commonWebpackConfig {
                outputFileName = "mifospay-wasm.js"
            }
        }
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val jsWasmMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(projects.mifospayShared)
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
            }
        }

        jsMain.get().dependsOn(jsWasmMain)
        wasmJsMain.get().dependsOn(jsWasmMain)
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}