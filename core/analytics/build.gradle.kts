plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.library.compose)
    alias(libs.plugins.mifospay.android.hilt)
}

android {
    namespace = "org.mifospay.core.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
