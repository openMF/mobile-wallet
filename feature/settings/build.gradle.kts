plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.settings"
}

dependencies {
    implementation(projects.core.data)
    // TODO: this should be removed
    implementation(projects.feature.auth)
    implementation(projects.feature.passcode)
    implementation(libs.mifosPasscode)
}