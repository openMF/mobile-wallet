plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.settings"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.passcode)
    implementation(projects.feature.auth)
    implementation(projects.feature.editpassword)
    implementation(libs.mifosPasscode)
    implementation(libs.androidx.appcompat)
}