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
    implementation(projects.feature.accounts)
    implementation(projects.feature.merchants)
    implementation(libs.accompanist.pager)
}