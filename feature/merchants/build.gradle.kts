plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.merchants"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.compose.material)
    implementation(projects.feature.history)
}