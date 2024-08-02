plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.receipt"
}

dependencies {
    // TODO:: this should be removed
    implementation(libs.squareup.okhttp)
    implementation(projects.core.data)
}