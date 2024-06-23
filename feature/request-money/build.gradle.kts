plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.request.money"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.zxing)
    implementation(libs.zxing.android.embedded)
    implementation(libs.coil.kt.compose)
}