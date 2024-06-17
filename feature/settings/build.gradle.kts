plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.settings"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.passcode)
    implementation(projects.feature.auth)
    implementation(projects.feature.editpassword)
    //implementation(libs.mifosPasscode)
    implementation("com.mifos.mobile:mifos-passcode:0.3.0@aar")
    implementation(libs.androidx.appcompat)
}