/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.mifospay.kmp.library)
    alias(libs.plugins.protobuf)
    id("kotlinx-serialization")
}

android {
    namespace = "org.mifos.mobilewallet.mifospay.core.datastore.proto"
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.protobuf.kotlin.lite)
            implementation(libs.kotlinx.serialization.core)
        }
    }
}