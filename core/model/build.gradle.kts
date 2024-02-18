plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.kotlin.parcelize)
    id("kotlinx-serialization")
}

android {
    namespace = "org.mifos.mobilewallet.core.model"
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

dependencies {
    api(libs.kotlinx.datetime)
    implementation(libs.jetbrains.kotlin.jdk7)

    // For Serialized name
    implementation(libs.squareup.retrofit.converter.gson)
}
