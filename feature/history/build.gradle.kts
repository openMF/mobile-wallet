plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.history"
}

dependencies {
    implementation(projects.feature.receipt)
    implementation(projects.core.data)
}