/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
import com.google.gms.googleservices.GoogleServicesPlugin.GoogleServicesPluginConfig
import org.mifospay.MifosBuildType
import org.mifospay.dynamicVersion

plugins {
    alias(libs.plugins.mifospay.android.application)
    alias(libs.plugins.mifospay.android.application.compose)
    alias(libs.plugins.mifospay.android.application.flavors)
    alias(libs.plugins.roborazzi)
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "org.mifospay"

    defaultConfig {
        applicationId = "org.mifospay"
        versionName = System.getenv("VERSION") ?: project.dynamicVersion
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "../keystores/release_keystore.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "Mifospay"
            keyAlias = System.getenv("KEYSTORE_ALIAS") ?: "key0"
            keyPassword = System.getenv("KEYSTORE_ALIAS_PASSWORD") ?: "Mifos@123"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = MifosBuildType.DEBUG.applicationIdSuffix
        }

        // Disabling proguard for now until
        // https://github.com/openMF/mobile-wallet/issues/1815 this issue is resolved
        release {
            isMinifyEnabled = false
            applicationIdSuffix = MifosBuildType.RELEASE.applicationIdSuffix
            isShrinkResources = false
            isDebuggable = false
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.mifospayShared)
    implementation(projects.core.data)
    implementation(projects.core.ui)

    // Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.runtime.tracing)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    runtimeOnly(libs.androidx.compose.runtime)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test)

    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit)

    testImplementation(kotlin("test"))
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath") {
        modules = true
        tree = true
    }
}

// Disable to fix memory leak and be compatible with the configuration cache.
configure<GoogleServicesPluginConfig> {
    disableVersionCheck = true
}