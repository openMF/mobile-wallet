import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.jetbrainsCompose)
}


kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))

                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.mifospay.desktop"
            packageName = "1.0.0"
        }
    }
}