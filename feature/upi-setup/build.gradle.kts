plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.upi_setup"
}

dependencies {
    implementation(projects.core.data)

    // need this because of M2 dependency in this module
    implementation("androidx.compose.material:material:1.6.0")
}