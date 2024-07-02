plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.qr"
}

dependencies {
    //Todo: Remove these after migration
    implementation("com.jakewharton:butterknife-annotations:10.2.3")
    implementation("com.jakewharton:butterknife:10.2.3@aar")
    implementation("me.dm7.barcodescanner:zxing:1.9.13")
    implementation("com.journeyapps:zxing-android-embedded:4.2.0")
    implementation(project(":core:data"))
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    implementation("com.google.guava:guava:27.0.1-android")
}