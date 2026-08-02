/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kmp.core.base.library.convention)
}

kotlin {
    js {
        useEsModules()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        useEsModules()
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.room.runtime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        desktopMain.dependencies {
            api(libs.androidx.sqlite.bundled)
        }
        nativeMain.dependencies {
            api(libs.androidx.sqlite.bundled)
        }
        androidMain.dependencies {
            api(libs.androidx.sqlite.bundled)
        }

        jsMain.dependencies {
            api(libs.androidx.sqlite.web)
            implementation(npm("sqlite-wasm-worker", layout.projectDirectory.dir("sqlite-wasm-worker").asFile))
            implementation(npm("sql-js-worker", layout.projectDirectory.dir("sql-js-worker").asFile))
        }
        wasmJsMain.dependencies {
            api(libs.androidx.sqlite.web)
            api(libs.kotlinx.browser)
            implementation(npm("sqlite-wasm-worker", layout.projectDirectory.dir("sqlite-wasm-worker").asFile))
            implementation(npm("sql-js-worker", layout.projectDirectory.dir("sql-js-worker").asFile))
        }
    }
}
