plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.hilt)
    alias(libs.plugins.kotlin.parcelize)
    id("kotlinx-serialization")
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

group = "com.github.ankurs287"

android {
    namespace = "org.mifos.mobilewallet.core"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.model)

    implementation(libs.squareup.retrofit2) {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude(module = "okhttp")
    }
    implementation(libs.squareup.retrofit.adapter.rxjava)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.logging.interceptor)

    implementation(libs.reactivex.rxjava.android)
    implementation(libs.reactivex.rxjava)

    implementation(libs.jetbrains.kotlin.jdk7)

    testImplementation(libs.junit)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
