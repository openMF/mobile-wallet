import org.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin that applies the CMP feature plugin and configures it.
 * This plugin applies the following plugins:
 * - kpt.kmp.library - Kotlin Multiplatform Library
 * - kpt.kmp.koin - Koin for Kotlin Multiplatform
 * - org.jetbrains.kotlin.plugin.compose - Kotlin Compose
 * - org.jetbrains.compose - Compose Multiplatform
 * - kpt.detekt.plugin - Detekt Plugin
 * - kpt.spotless.plugin - Spotless Plugin
 *
 */
class CMPFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("org.convention.kmp.library")
                apply("org.convention.kmp.koin")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
                apply("org.convention.detekt.plugin")
                apply("org.convention.spotless.plugin")
            }

            // Compose Multiplatform UI-test infra (RULE-KMP-COMPOSE-UITEST-001):
            // commonTest gets the multiplatform `runComposeUiTest` API; the desktop (JVM)
            // target gets the JUnit4-backed runner that actually executes it (CI-runnable).
            // Referenced by coordinate — build-logic does not expose the
            // org.jetbrains.compose plugin types, only its plugin id.
            val composeVersion = libs.findVersion("compose-plugin").get().requiredVersion

            dependencies {
                add("commonTestImplementation", "org.jetbrains.compose.ui:ui-test:$composeVersion")
                add(
                    "desktopTestImplementation",
                    "org.jetbrains.compose.ui:ui-test-junit4:$composeVersion",
                )

                add("commonMainImplementation", project(":core:ui"))
                add("commonMainImplementation", project(":core-base:ui"))
                add("commonMainImplementation", project(":core:designsystem"))
                add("commonMainImplementation", project(":core-base:designsystem"))
                add("commonMainImplementation", project(":core:data"))
                add("commonMainImplementation", project(":core-base:designsystem"))
                add("commonMainImplementation", project(":core:analytics"))

                add("commonMainImplementation", libs.findLibrary("koin.compose").get())
                add("commonMainImplementation", libs.findLibrary("koin.compose.viewmodel").get())

                add("commonMainImplementation", libs.findLibrary("jb.composeRuntime").get())
                add("commonMainImplementation", libs.findLibrary("jb.composeViewmodel").get())
                add("commonMainImplementation", libs.findLibrary("jb.lifecycleViewmodel").get())
                add("commonMainImplementation", libs.findLibrary("jb.lifecycle.compose").get())

                add(
                    "commonMainImplementation",
                    libs.findLibrary("jb.lifecycleViewmodelSavedState").get(),
                )
                add("commonMainImplementation", libs.findLibrary("jb.savedstate").get())
                add("commonMainImplementation", libs.findLibrary("jb.bundle").get())
                add("commonMainImplementation", libs.findLibrary("jb.composeNavigation").get())
                add(
                    "commonMainImplementation",
                    libs.findLibrary("kotlinx.collections.immutable").get(),
                )

                add("androidMainImplementation", platform(libs.findLibrary("koin-bom").get()))
                add("androidMainImplementation", libs.findLibrary("koin-android").get())
                add("androidMainImplementation", libs.findLibrary("koin.androidx.compose").get())

                add("androidMainImplementation", libs.findLibrary("koin.android").get())
                add("androidMainImplementation", libs.findLibrary("koin.androidx.navigation").get())
                add("androidMainImplementation", libs.findLibrary("koin.androidx.compose").get())
                add("androidMainImplementation", libs.findLibrary("koin.core.viewmodel").get())

            }
        }
    }
}
