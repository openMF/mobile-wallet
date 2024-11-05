pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
    id("org.ajoberstar.reckon.settings") version("0.18.3")
}

extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
    setDefaultInferredScope("patch")
    stages("beta", "rc", "final")
    setScopeCalc { java.util.Optional.of(org.ajoberstar.reckon.core.Scope.PATCH) }
    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())
    setTagWriter { it.toString() }
}

rootProject.name = "mobile-wallet"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":mifospay-shared")
include(":mifospay-android")
include(":mifospay-desktop")
include(":mifospay-web")

include(":core:data")
include(":core:domain")
include(":core:datastore")
include(":core:designsystem")
include(":core:ui")
include(":core:common")
include(":core:network")
include(":core:network")
include(":core:model")
include(":core:datastore-proto")
include(":core:analytics")

include(":feature:home")
include(":feature:history")
include(":feature:receipt")
include(":feature:faq")
include(":feature:auth")
include(":feature:make-transfer")
include(":feature:send-money")
include(":feature:notification")
include(":feature:editpassword")
include(":feature:kyc")
include(":feature:savedcards")
include(":feature:invoices")
include(":feature:invoices")
include(":feature:settings")
include(":feature:profile")
include(":feature:finance")
include(":feature:merchants")
include(":feature:accounts")
include(":feature:standing-instruction")
include(":feature:payments")
include(":feature:request-money")
include(":feature:upi-setup")
include(":feature:qr")

include(":libs:mifos-passcode")
