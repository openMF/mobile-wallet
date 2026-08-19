/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
import com.android.build.api.instrumentation.InstrumentationScope
import org.convention.dynamicVersion
import org.convention.resolveSecretPath

plugins {
    alias(libs.plugins.android.application.convention)
    alias(libs.plugins.android.application.compose.convention)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.ksp)
}

val appId: String = libs.versions.appId.get()
val appDisplayName: String = libs.versions.appDisplayName.get()

android {
    // Namespace is the STABLE code-gen package (BuildConfig / R live here) — deliberately fixed to the
    // module's own source package, NOT the white-label applicationId. Decoupling the two is what lets a
    // fork rebrand `applicationId` (market identity) with zero source edits while `BuildConfig` stays at a
    // stable import path (`cmp.android.app.BuildConfig`, same package as this module's Kotlin).
    namespace = "cmp.android.app"

    defaultConfig {
        applicationId = appId
        // app_name is injected from libs.versions.toml — no hardcoded strings.xml entry needed
        resValue("string", "app_name", appDisplayName)
        versionName = System.getenv("VERSION") ?: project.dynamicVersion
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // v2 Play App Signing model — Gradle signs release AABs with the UPLOAD key.
            // Path resolved by the ONE resolver (build-secrets → secrets/LAYOUT.yaml key `upload_keystore`
            // → secrets/live/android/keystores/upload_keystore.keystore, sample fallback) — never hardcoded
            // here. KEYSTORE_PATH env overrides (CI materializes it via /secrets before a deploy).
            storeFile = System.getenv("KEYSTORE_PATH")?.let { file(it) }
                ?: project.resolveSecretPath("upload_keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "Wizard@123"
            keyAlias = System.getenv("KEYSTORE_ALIAS") ?: "kmp-project-template"
            keyPassword = System.getenv("KEYSTORE_ALIAS_PASSWORD") ?: "Wizard@123"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        // debug/staging/release are registered by org.convention.kmp.flavors via
        // KMPFlavorsConventionPlugin (isDebuggable, applicationIdSuffix, isMinifyEnabled).
        // Only Android-app-specific settings that the plugin doesn't own live here.
        getByName("staging") {
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isShrinkResources = true
            isJniDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
        resValues = true
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

    // TODO:: Workaround for Ktor(3.2.0) R8/ProGuard Issue
    androidComponents {
        onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                FieldSkippingClassVisitor.Factory::class.java,
                scope = InstrumentationScope.ALL,
            ) { params ->
                params.classes.add("io.ktor.client.plugins.Messages")
            }
        }
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))

    implementation(projects.cmpShared)
    implementation(projects.core.ui)
    implementation(projects.core.firebase)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.core.platform)
    implementation(projects.sync)

    // Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    implementation(libs.kermit.koin)

    implementation(libs.app.update.ktx)
    implementation(libs.app.update)

    implementation(libs.coil.kt)

    implementation(libs.filekit.core)
    implementation(libs.filekit.compose)
    implementation(libs.filekit.dialog.compose)
    implementation(libs.filekit.coil)

    runtimeOnly(libs.androidx.compose.runtime)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test)

    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.test.ext.junit)

    testImplementation(libs.koin.test.junit4)
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath") {
        modules = true
        tree = true
    }
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}
