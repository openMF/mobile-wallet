plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.payments"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.history)
    implementation(projects.feature.invoices)
    implementation(projects.feature.sendMoney)
    implementation(projects.feature.standingInstruction)

    implementation(libs.accompanist.pager)
}