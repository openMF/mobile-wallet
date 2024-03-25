plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifos.mobilewallet.mifospay.ui"
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)

    api(libs.androidx.metrics)
    api(projects.core.analytics)
    api(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.androidx.browser)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}