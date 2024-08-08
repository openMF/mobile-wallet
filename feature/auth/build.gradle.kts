plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.auth"
    buildFeatures {
        buildConfig = true
    }
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.passcode)

    implementation(libs.compose.country.code.picker)
    // TODO:: this should be removed
    implementation(libs.compose.material)

    // Credentials Manager
    implementation(libs.androidx.credentials)
    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    implementation(libs.mifosPasscode)

    implementation(libs.play.services.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}