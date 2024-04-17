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

    // Credentials Manager
    implementation("androidx.credentials:credentials:1.2.1")
    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation("androidx.credentials:credentials-play-services-auth:1.2.1")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    implementation("com.mifos.mobile:mifos-passcode:0.3.0")

    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // we need it for country picker library
    implementation("androidx.compose.material:material:1.6.0")
    implementation(libs.compose.country.code.picker) // remove after moving auth code to module

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}