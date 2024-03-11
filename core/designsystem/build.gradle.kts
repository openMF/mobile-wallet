plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.library.compose)
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "org.mifos.mobilewallet.mifospay.designsystem"
}

dependencies {
    implementation(projects.core.model)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.hilt.navigation.compose)

    debugApi(libs.androidx.compose.ui.tooling)
}