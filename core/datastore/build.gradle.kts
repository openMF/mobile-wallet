plugins {
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.hilt)
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

android {
    namespace = "org.mifos.mobilewallet.mifospay.core.datastore"
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
    api(libs.kotlinx.datetime)
    api(libs.androidx.dataStore.core)
    api(projects.core.datastoreProto)
    api(projects.core.common)
    api(projects.core.model)
}