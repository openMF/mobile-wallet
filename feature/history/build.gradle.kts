plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.history"
}

dependencies {
    implementation(projects.feature.receipt)
    implementation(libs.compose.material)
    implementation(projects.core.data)
    implementation(libs.androidx.appcompat)
}