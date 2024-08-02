plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.bank.accounts"
}

dependencies {
    implementation(projects.core.data)
    // TODO:: this should be removed
    implementation(libs.compose.material)
    implementation(projects.feature.upiSetup)
}