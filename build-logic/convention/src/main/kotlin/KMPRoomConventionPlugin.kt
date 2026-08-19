import androidx.room3.gradle.RoomExtension
import org.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin that configures Room 3 for Kotlin Multiplatform modules.
 *
 * Applies the `androidx.room3` Gradle plugin and `com.google.devtools.ksp`, sets the
 * schema export directory, adds the `room3-runtime` dependency, and registers the
 * Room KSP compiler for all six KMP targets (Android, Desktop, iOS Arm64,
 * iOS Simulator Arm64, JS, WasmJS).
 *
 * Usage: apply via `alias(libs.plugins.mifos.kmp.room)` in a module's `build.gradle.kts`.
 */
class KMPRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.room3")
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                "commonMainImplementation"(libs.findLibrary("androidx.room.runtime").get())

                listOf(
                    "kspAndroid",
                    "kspDesktop",
                    "kspIosArm64",
                    "kspIosSimulatorArm64",
                    "kspJs",
                    "kspWasmJs",
                ).forEach { platform ->
                    add(platform, libs.findLibrary("androidx.room.compiler").get())
                }
            }
        }
    }
}
