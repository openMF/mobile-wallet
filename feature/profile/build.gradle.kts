import com.android.build.gradle.internal.tasks.ApplicationIdWriterTask

plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.profile"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.squareup.okhttp)
    implementation(libs.compose.country.code.picker)
    implementation(libs.compose.material)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.appcompat)
}