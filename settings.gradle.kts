pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "mobile-wallet"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":mifospay")
include(":core")
include(":main-core:datastore")
include(":feature:auth")
include(":main-core:designsystem")
include(":main-core:ui")
include(":main-core:data")
include(":main-core:common")
