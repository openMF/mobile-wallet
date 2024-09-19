import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.mifospay.libs

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("mifospay.android.library")

            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    // set custom test runner
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                testOptions.animationsDisabled = true
            }

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))

                add("implementation", project(":libs:material3-navigation"))

                add("implementation", libs.findLibrary("kotlinx.collections.immutable").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())

                add("implementation", platform(libs.findLibrary("koin-bom").get()))
                add("implementation", libs.findLibrary("koin-android").get())
                add("implementation", libs.findLibrary("koin.androidx.compose").get())

                add("androidTestImplementation", libs.findLibrary("androidx.lifecycle.runtimeTesting").get())

                add("testImplementation", kotlin("test"))

                add("testImplementation", libs.findLibrary("koin.test").get())
                add("testImplementation", libs.findLibrary("koin.test.junit4").get())

                add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
                add("androidTestImplementation", libs.findLibrary("androidx.navigation.testing").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())
            }
        }
    }
}
