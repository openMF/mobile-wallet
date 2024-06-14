plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.notification"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.androidx.appcompat)

    //we need this for pull to refresh implementation
    implementation("androidx.compose.material:material:1.6.0")
}