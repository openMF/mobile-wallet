plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.profile"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.squareup.okhttp)
    implementation(libs.compose.country.code.picker)
    implementation(libs.compose.material)
    implementation(libs.coil.kt.compose)
    implementation(projects.mifospay)
}