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
            packageVersion = "1.0.0"
            windows {
                // a version for all Windows distributables
                packageVersion = "1.0.0"
                // a version only for the msi package
                msiPackageVersion = "1.0.0"
                // a version only for the exe package
                exePackageVersion = "1.0.0"
                menu = true
            }
        }
    }
}