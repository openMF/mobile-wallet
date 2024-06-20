plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.bank.accounts"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.compose.material)
    implementation(libs.androidx.appcompat)

    //Remove the following implementations after completing migration
    implementation(projects.mifospay)
    implementation("com.mifos.mobile:mifos-passcode:0.3.0@aar")
}