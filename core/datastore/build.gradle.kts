plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.hilt)
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

android {
    namespace = "org.mifos.mobilewallet.datastore"
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {

}