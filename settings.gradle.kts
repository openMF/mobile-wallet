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

    // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) was removed as it centralized repository configuration in this
    // file to ensure that all subprojects use the same repositories. As a result Gradle has deprecated the use of project-level repositories in favor of settings-level repositories,
    // leading to warnings or errors if project repositories are still used when RepositoriesMode.FAIL_ON_PROJECT_REPOS is enabled.
    // Find the discussion at https://stackoverflow.com/questions/69163511/build-was-configured-to-prefer-settings-repositories-over-project-repositories-b

    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://www.jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "mobile-wallet"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":mifospay")

include(":core:data")
include(":core:datastore")
include(":core:designsystem")
include(":core:ui")
include(":core:common")
include(":core:network")
include(":core:network")
include(":core:model")
include(":core:datastore-proto")
include(":core:analytics")

include(":feature:history")
include(":feature:receipt")
include(":feature:faq")
include(":feature:passcode")
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
include(":feature:home")
include(":shared")
