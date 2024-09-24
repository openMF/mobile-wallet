
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.mifospay.libs

class CMPFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("mifospay.kmp.library")
                apply("mifospay.kmp.inject")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }

            dependencies {
                add("commonMainImplementation", project(":core:ui"))
                add("commonMainImplementation", project(":core:designsystem"))
                add("commonMainImplementation", project(":core:data"))
                add("commonMainImplementation", libs.findLibrary("jetbrains.compose.viewmodel").get())
                add("commonMainImplementation", libs.findLibrary("jetbrains.compose.navigation").get())
                add("commonMainImplementation", libs.findLibrary("kotlinx.collections.immutable").get())

                add("androidMainImplementation", project(":libs:material3-navigation"))

                add("androidMainImplementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("androidMainImplementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                add("androidMainImplementation", libs.findLibrary("androidx.tracing.ktx").get())

                add("androidMainImplementation", platform(libs.findLibrary("koin-bom").get()))
                add("androidMainImplementation", libs.findLibrary("koin-android").get())
                add("androidMainImplementation", libs.findLibrary("koin.androidx.compose").get())

                add("androidMainImplementation", libs.findLibrary("koin.android").get())
                add("androidMainImplementation", libs.findLibrary("koin.androidx.navigation").get())
                add("androidMainImplementation", libs.findLibrary("koin.androidx.compose").get())
                add("androidMainImplementation", libs.findLibrary("koin.core.viewmodel").get())

                add("androidTestImplementation", libs.findLibrary("koin.test.junit4").get())

                add("androidDebugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.navigation.testing").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.test.core").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.test.ext").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.test.junit").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.test.runner").get())
                add("androidInstrumentedTestImplementation", libs.findLibrary("androidx.test.espresso.core").get())
            }
        }
    }
}
