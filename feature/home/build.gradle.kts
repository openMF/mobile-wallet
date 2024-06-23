plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.home"
}

dependencies {
    implementation(projects.feature.history)
    implementation(projects.core.data)

    //remove this after optimizing HistoryContract
    implementation(projects.mifospay)
}