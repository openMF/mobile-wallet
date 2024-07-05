plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.bank.accounts"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.compose.material)
    implementation(libs.androidx.appcompat)
    implementation(projects.feature.upiSetup)
}