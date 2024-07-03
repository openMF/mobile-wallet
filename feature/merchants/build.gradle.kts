plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.merchants"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.compose.material)
    implementation(projects.feature.history)

    //Todo: Remove these after migration of MerchantTransferActivity
    implementation("com.jakewharton:butterknife-annotations:10.2.3")
    implementation("com.jakewharton:butterknife:10.2.3@aar")
    implementation("com.mifos.mobile:mifos-passcode:0.3.0@aar")
}