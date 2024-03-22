plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifos.mobilewallet.mifospay.feature.auth"
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

dependencies {
    implementation(projects.core.data)

    implementation(libs.compose.country.code.picker)

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