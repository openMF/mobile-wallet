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

include(":cmp-shared")
include(":cmp-android")
include(":cmp-desktop")
include(":cmp-web")

include(":core:data")
include(":core:domain")
include(":core:datastore")
include(":core:designsystem")
include(":core:ui")
include(":core:common")
include(":core:network")
include(":core:network")
include(":core:model")
include(":core:analytics")

include(":core-base:datastore")
include(":core-base:common")
include(":core-base:database")
include(":core-base:network")
include(":core-base:designsystem")
include(":core-base:platform")
include(":core-base:ui")
include(":core-base:analytics")

include(":feature:home")
include(":feature:history")
include(":feature:receipt")
include(":feature:faq")
include(":feature:auth")
include(":feature:transfer-intrabank")
include(":feature:transfer-interbank")
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
include(":feature:beneficiary")
include(":feature:standing-instruction")
include(":feature:payments")
include(":feature:upi-setup")
include(":feature:mpay-qr")
include(":feature:mpay-qr-scan")
include(":feature:fast-mpay")
include(":feature:passcode")

include(":libs:mifos-passcode")
include(":feature:onboarding-language")
