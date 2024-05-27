plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.send.money"
}

dependencies {
    implementation(projects.core.data)

    // we need it for country picker library
    implementation("androidx.compose.material:material:1.6.0")
    implementation(libs.compose.country.code.picker) // remove after moving auth code to module

    // Google Bar code scanner
    implementation(libs.google.play.services.code.scanner)
}
