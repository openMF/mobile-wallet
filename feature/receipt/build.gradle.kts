plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.receipt"
}

dependencies {
    implementation(libs.squareup.okhttp)
    implementation(projects.core.data)
    implementation(libs.androidx.appcompat)
    implementation(projects.feature.passcode)
    implementation(projects.mifospay)
    implementation(libs.mifosPasscode)
}