plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.qr"
}

dependencies {
    implementation(libs.zxing)
    implementation(projects.core.data)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    // TODO:: this should be removed
    implementation("com.google.guava:guava:27.0.1-android")
}