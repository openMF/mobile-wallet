plugins {
    alias(libs.plugins.cmp.feature.convention)
}

android {
    namespace = "org.mifospay.feature.passcode"
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.coreBase.datastore)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
    }
}