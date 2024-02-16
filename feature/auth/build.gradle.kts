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

    implementation("com.mifos.mobile:mifos-passcode:0.3.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}