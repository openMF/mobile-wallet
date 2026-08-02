import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "org.convention.buildlogic"

// Configure the build-logic plugins to target JDK 19
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    compileOnly(libs.kover.gradlePlugin)
    implementation(libs.kmp.product.flavors.plugin)
    // worker-kmp app plugin — must be `implementation` (not `compileOnly`) so its
    // META-INF/gradle-plugins descriptor is on the build-logic runtime classpath,
    // letting WorkerComposeConventionPlugin apply it via pluginManager.apply(id).
    implementation(libs.worker.app.plugin)
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
            id = "org.convention.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "org.convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("kmpFlavors") {
            id = "org.convention.kmp.flavors"
            implementationClass = "KMPFlavorsConventionPlugin"
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

        register("kmpCoreBaseLibrary") {
            id = "org.convention.kmp.core.base.library"
            implementationClass = "KMPCoreBaseLibraryConventionPlugin"
        }

        // Static Analysis & Formatting Plugins
        register("detekt") {
            id = "org.convention.detekt.plugin"
            implementationClass = "DetektConventionPlugin"
            description = "Configures detekt for the project"
        }
        register("spotless") {
            id = "org.convention.spotless.plugin"
            implementationClass = "SpotlessConventionPlugin"
            description = "Configures spotless for the project"
        }
        register("ktlint") {
            id = "org.convention.ktlint.plugin"
            implementationClass = "KtlintConventionPlugin"
            description = "Configures kotlinter for the project"
        }
        register("kover") {
            id = "org.convention.kover.plugin"
            implementationClass = "KoverConventionPlugin"
            description = "Applies the kover code-coverage plugin to a module. Chained from base convention plugins (Android/KMP/CMP)."
        }
        register("gitHooks") {
            id = "org.convention.git.hooks"
            implementationClass = "GitHooksConventionPlugin"
            description = "Installs git hooks for the project"
        }

        //  Room Plugin
        register("KMPRoom"){
            id = "mifos.kmp.room"
            implementationClass = "KMPRoomConventionPlugin"
            description = "Configures Room for the project"
        }

        // Fork identity sync
        register("forkSyncConfig") {
            id = "org.convention.fork.sync-config"
            implementationClass = "SyncForkConfigPlugin"
            description = "Registers syncForkConfig task — syncs libs.versions.toml identity to iOS xcconfig, local.properties, gradle.properties"
        }
        register("workerCompose") {
            id = "org.convention.worker.compose"
            implementationClass = "WorkerComposeConventionPlugin"
            description = "Wires the worker-kmp app plugin + the worker-compose-all bundle into a Compose Multiplatform module. Use on the module that hosts the consumer-facing workers + WorkScheduler."
        }
        // Supabase Config Plugin
        register("supabaseConfig") {
            id = "org.convention.kmp.supabase.config"
            implementationClass = "SupabaseConfigConventionPlugin"
            description = "Generates Supabase credentials from secrets file"
        }
    }
}
