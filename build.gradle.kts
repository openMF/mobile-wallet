// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.google.oss.licenses.plugin) {
            exclude(group = "com.google.protobuf")
        }
        // No manual R8 override: AGP 9.3.x bundles a matched R8 (9.3+/9.4 line) that
        // reads this repo's Kotlin 2.4 class metadata natively. The former
        // `com.android.tools:r8:9.1.31` pin dated to the AGP 8.12.3 / Kotlin 2.3.x era
        // (old bundled R8 topped out at Kotlin metadata 2.1) and is obsolete — pinning an
        // older R8 under a newer AGP only risks a version-skew failure. Let AGP own R8.
    }
}

plugins {
    // SKIE — declared on the root classpath (apply false) so cmp-shared can apply it
    // for the Swift-enhanced XCFramework export (E6). Replaces the removed Kotlin
    // CocoaPods plugin (`libs.plugins.kotlinCocoapods`).
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.moduleGraph) apply true
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.roborazzi) apply false
    // Multiplatform plugins
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.wire) apply false
    alias(libs.plugins.ktrofit) apply false

    alias(libs.plugins.room) apply false

    // Kover — root-level aggregation.
    //
    // Per-module kover application happens via `org.convention.kover.plugin`
    // chained from base convention plugins (AndroidApplication / KMPLibrary /
    // KMPCoreBaseLibrary). cmp-desktop applies it directly.
    //
    // Aggregation list (below) is auto-discovered from `subprojects` — any new
    // module under :feature:*, :core:*, or :core-base:* is picked up with zero
    // manual maintenance.
    //
    // Filter/verify config (further below) stays inline at root because moving
    // it into a build-logic convention plugin would require kover-gradle-plugin
    // on build-logic's runtime classpath, which transitively conflicts with
    // AGP's kotlin-gradle-plugin (kover issue #135, confirmed by trial). Kover's
    // own multi-module KMP guide recommends root-level config for the same
    // reason: https://kotlin.github.io/kotlinx-kover/gradle-plugin/#multi-module-kotlin-multiplatform-project
    //
    // Tasks: ./gradlew koverHtmlReport | koverXmlReport | koverVerify
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.kover.convention)
    id("org.convention.fork.sync-config")
}


object DynamicVersion {
    fun setDynamicVersion(file: File, version: String) {
        val cleanedVersion = version.split('+')[0]
        file.writeText(cleanedVersion)
    }
}

tasks.register("versionFile") {
    val file = File(projectDir, "version.txt")

    DynamicVersion.setDynamicVersion(file, project.version.toString())
}

// Task to print all the module paths in the project e.g. :core:data
// Used by module graph generator script
tasks.register("printModulePaths") {
    subprojects {
        if (subprojects.isEmpty()) {
            println(this.path)
        }
    }
}

// Force consistent versions across all subprojects to fix KLIB resolver duplicate warnings
// The conflict is between org.jetbrains.androidx.* (CMP) and androidx.* (Google) transitive deps
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            // Replace Google androidx.lifecycle with JetBrains fork for non-Android targets.
            // Pin to the version shipped WITH compose-multiplatform 1.11.1 — Compose's
            // emitted IR symbols (LocalViewModelStoreOwner etc.) require this exact
            // version. Without strictly{}, Gradle prefers stable 2.9.6 over pre-release
            // 2.11.0-beta01 and produces 'IrPropertySymbolImpl is already bound' on
            // Kotlin/JS compile.
            if (requested.group == "org.jetbrains.androidx.lifecycle") {
                useVersion("2.11.0-beta01")
                because("Compose Multiplatform 1.11.1 bundles 2.11.0-beta01")
            }
            if (requested.group == "org.jetbrains.androidx.savedstate") {
                useVersion("1.3.6")
            }
        }
    }

    // Gradle 9+ defaults Test.failOnNoDiscoveredTests to true. AGP unit-test
    // tasks (testDemoDebugUnitTest, testProdReleaseUnitTest, etc.) then fail
    // on KMP `androidUnitTest` source sets that contain expect/actual TEST
    // HELPERS but no @Test classes — those test classes legitimately live in
    // `commonTest` or `desktopTest`. Disabling per-task unblocks the kover
    // coverage gate without weakening real-test signal.
    tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
        failOnNoDiscoveredTests = false
    }
}

// Configuration for CMP module dependency graph
moduleGraphAssert {
    configurations += setOf("commonMainImplementation", "commonMainApi")
    configurations += setOf("androidMainImplementation", "androidMainApi")
    configurations += setOf("desktopMainImplementation", "desktopMainApi")
    configurations += setOf("jsMainImplementation", "jsMainApi")
    configurations += setOf("nativeMainImplementation", "nativeMainApi")
    configurations += setOf("wasmJsMainImplementation", "wasmJsMainApi")
}

