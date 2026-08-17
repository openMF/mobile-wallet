// ── Workspace Library Linker (managed by /lib-integrate) ──────────────────
// Edit lib-integrate.properties to add/remove libraries. Never edit this block.
// Path-existence guard: if library not cloned locally → silently uses Maven Central.
// Groups libraries by path so multiple modules from same build use one includeBuild.
val libProps = java.util.Properties().apply {
    file("lib-integrate.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
data class LibEntry(val artifact: String, val module: String)
val pathToEntries = mutableMapOf<String, MutableList<LibEntry>>()
libProps.stringPropertyNames()
    .filter { it.endsWith(".local") && libProps[it] == "true" }
    .forEach { key ->
        val lib      = key.removeSuffix(".local")
        val path     = libProps["$lib.path"]     as? String ?: return@forEach
        val module   = libProps["$lib.module"]   as? String ?: return@forEach
        val artifact = libProps["$lib.artifact"] as? String ?: return@forEach
        // Normalize ".." segments before existence check -- Java's File.exists()
        // doesn't collapse ".." past filesystem root on Windows, so the bare
        // file() check can spuriously pass on CI runners.
        val resolved = settingsDir.toPath().resolve(path).normalize().toFile()
        if (!resolved.isDirectory) {
            println("📦 [lib-integrate] $lib → Maven Central (source not found at $path)")
            return@forEach
        }
        println("⚡ [lib-integrate] $lib → local source ($path)")
        pathToEntries.getOrPut(path) { mutableListOf() }.add(LibEntry(artifact, module))
    }
pathToEntries.forEach { (path, entries) ->
    includeBuild(path) {
        dependencySubstitution {
            entries.forEach { (artifact, module) ->
                substitute(module(artifact)).using(project(module))
            }
        }
    }
}
// ── End lib-integrate managed block ───────────────────────────────────────

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenLocal()
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
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_PROJECT
    repositories {
        mavenLocal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Fork-only: jitpack hosts a few mifospay-tree artifacts (fineract SDK
        // legacy, etc.) so keep it available even after the template migration.
        maven("https://www.jitpack.io")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
    id("org.ajoberstar.reckon.settings") version("0.19.2")
}

buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
    }
}

extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
    setDefaultInferredScope("patch")
    stages("beta", "rc", "final")
    setScopeCalc { java.util.Optional.of(org.ajoberstar.reckon.core.Scope.PATCH) }
    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())
    setTagWriter { it.toString() }
}

// Project name is driven by fork.project.name in gradle.properties (written by syncForkConfig).
// Fallback keeps the fork's canonical name so a clean checkout builds without running syncForkConfig first.
rootProject.name = providers.gradleProperty("fork.project.name").getOrElse("mifos-pay")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// ── Umbrella / packaging modules ─────────────────────────────────────────────
include(":cmp-shared")
include(":cmp-android")
include(":cmp-desktop")
include(":cmp-web")
include(":cmp-navigation")

// ── Template core modules (offline-first stack shipped by kmp-project-template) ─
include(":core:analytics")
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:platform")
include(":core:store")
include(":core:ui")

// ── Template core-base modules (portable base kit) ──────────────────────────
include(":core-base:analytics")
include(":core-base:common")
include(":core-base:database")
include(":core-base:datastore")
include(":core-base:designsystem")
include(":core-base:network")
include(":core-base:observability")
include(":core-base:platform")
include(":core-base:security")
include(":core-base:store")
include(":core-base:ui")

// ── Sync module (background job orchestration) ──────────────────────────────
include(":sync")

// ── Fork feature modules (mifos-pay product surfaces) ───────────────────
include(":feature:home")
include(":feature:history")
include(":feature:receipt")
include(":feature:faq")
include(":feature:auth")
include(":feature:make-transfer")
include(":feature:send-money")
include(":feature:transfer-intrabank")
include(":feature:transfer-interbank")
include(":feature:notification")
include(":feature:editpassword")
include(":feature:kyc")
include(":feature:savedcards")
include(":feature:invoices")
include(":feature:settings")
include(":feature:profile")
include(":feature:finance")
include(":feature:merchants")
include(":feature:accounts")
include(":feature:beneficiary")
include(":feature:standing-instruction")
include(":feature:payments")
include(":feature:upi-setup")
include(":feature:qr")
include(":feature:autopay")
include(":feature:mpay-qr")
include(":feature:mpay-qr-scan")
include(":feature:fast-mpay")
include(":feature:passcode")
include(":feature:pocket")

// ── Fork libraries ──────────────────────────────────────────────────────────
include(":libs")
include(":libs:mifos-loans")

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    This project requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
