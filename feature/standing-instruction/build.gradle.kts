plugins {
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.standing.instruction"
}

dependencies {
    implementation(projects.core.data)

    // Google Bar code scanner
    implementation(libs.google.play.services.code.scanner)
}