import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "org.mifospay.buildlogic"

// Configure the build-logic plugins to target JDK 19
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.spotless.gradle)
    implementation(libs.truth)
    compileOnly(libs.androidx.room.gradle.plugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin)
    compileOnly(libs.firebase.performance.gradlePlugin)

    // Keystore management dependencies
    implementation(libs.github.api)
    implementation(libs.okhttp)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.commons.codec)

    // Test dependencies for keystore management
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.platform.junit.platform.launcher)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }

    // Configure JUnit 5 for testing keystore management functionality
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

gradlePlugin {
    plugins {
        // Android Plugins
        register("androidApplicationCompose") {
            id = "mifospay.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "mifospay.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidFlavors") {
            id = "mifospay.android.application.flavors"
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }

        register("androidFirebase") {
            id = "org.convention.android.application.firebase"
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }

        register("androidLint") {
            id = "org.convention.android.application.lint"
            implementationClass = "AndroidLintConventionPlugin"
        }

        // KMP & CMP Plugins
        register("cmpFeature") {
            id = "org.convention.cmp.feature"
            implementationClass = "CMPFeatureConventionPlugin"
        }

        register("kmpKoin") {
            id = "org.convention.kmp.koin"
            implementationClass = "KMPKoinConventionPlugin"
        }
        register("kmpLibrary") {
            id = "org.convention.kmp.library"
            implementationClass = "KMPLibraryConventionPlugin"
        }

        // Static Analysis & Formatting Plugins
        register("detekt") {
            id = "mifos.detekt.plugin"
            implementationClass = "MifosDetektConventionPlugin"
            description = "Configures detekt for the project"
        }
        register("spotless") {
            id = "mifos.spotless.plugin"
            implementationClass = "MifosSpotlessConventionPlugin"
            description = "Configures spotless for the project"
        }
        register("ktlint") {
            id = "mifos.ktlint.plugin"
            implementationClass = "MifosKtlintConventionPlugin"
            description = "Configures kotlinter for the project"
        }
        register("gitHooks") {
            id = "mifos.git.hooks"
            implementationClass = "MifosGitHooksConventionPlugin"
            description = "Installs git hooks for the project"
        }

//        Room Plugin
        register("KMPRoom"){
            id = "mifos.kmp.room"
            implementationClass = "KMPRoomConventionPlugin"
            description = "Configures Room for the project"
        }

        // NEW ===============================

        register("keystoreManagement") {
            id = "org.convention.keystore.management"
            implementationClass = "KeystoreManagementConventionPlugin"
            description = "Configures keystore management tasks for the project"
        }

    }
}