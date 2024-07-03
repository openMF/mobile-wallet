plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.kyc"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.compose.material)
    implementation(libs.sheets.compose.dialogs.core)
    implementation(libs.sheets.compose.dialogs.calender)
    implementation(libs.compose.country.code.picker)
    implementation(libs.squareup.okhttp)
}