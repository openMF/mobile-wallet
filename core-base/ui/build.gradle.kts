/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.core.base.library.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    // Module-local BuildKonfig so AppInfo.appDisplayName (the SINGLE common-code accessor for the
    // app's user-facing display name) reads the fork's app.display.name WITHOUT a hardcoded string
    // resource. Same mechanism feature/settings + core/network use (white-label seam).
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            api(libs.androidx.metrics)
            implementation(libs.androidx.compose.runtime)
        }

        commonMain.dependencies {
            implementation(projects.coreBase.store)
            implementation(projects.coreBase.designsystem)
            implementation(libs.cmp.network.monitor.compose)
            implementation(libs.cmp.intent.launcher)

            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.uiToolingPreview)

            // Compottie — first-class Lottie support for ScreenStateVisual.Lottie.
            // `api` so apps that pass ScreenStateVisual.Lottie(spec = { ... }) can build
            // a LottieCompositionSpec without re-declaring the dep.
            api(libs.compottie)
            api(libs.compottie.resources)

            implementation(libs.jb.composeViewmodel)
            implementation(libs.jb.lifecycle.compose)
            implementation(libs.jb.lifecycleViewmodel)
            implementation(libs.jb.composeNavigation)
            implementation(libs.jb.lifecycleViewmodelSavedState)

            implementation(libs.coil.kt)
            implementation(libs.coil.kt.compose)

            implementation(libs.filekit.core)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.coil)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.androidx.compose.ui.test)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
        }

        jvmJsCommonMain.dependencies {
            implementation(libs.filekit.core)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.coil)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.core.base.ui.generated.resources"
}

// Fork app display name → `kpt.core.base.ui.BuildKonfig.APP_DISPLAY_NAME`, read from
// `gradle/fork.properties#app.display.name` — the build-bridge that syncForkConfig generates from the
// SoT `app-profile/app.yaml#identity.app_name`. AppInfo.appDisplayName exposes it as the single
// common-code read point, so a fork rebrands in app-profile, not in per-feature strings.xml.
val coreBaseUiForkProps = Properties().apply {
    val f = rootProject.file("gradle/fork.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

buildkonfig {
    packageName = "kpt.core.base.ui"
    defaultConfigs {
        buildConfigField(
            STRING,
            "APP_DISPLAY_NAME",
            coreBaseUiForkProps.getProperty("app.display.name").orEmpty().ifBlank { "App Toolkit" },
        )
    }
}
