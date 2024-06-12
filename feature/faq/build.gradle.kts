plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "com.example.faq"
}

dependencies {
    implementation(libs.androidx.appcompat)
}