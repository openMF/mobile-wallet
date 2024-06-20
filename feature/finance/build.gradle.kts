plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.finance"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.kyc)
    implementation(projects.feature.savedcards)

    //Todo:Remove this after moving account and merchant screen
    implementation(project(":mifospay"))
}