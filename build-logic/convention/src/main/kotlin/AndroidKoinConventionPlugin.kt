import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.mifospay.libs


class AndroidKoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.koin")
            }

            dependencies {
                "implementation"(libs.findLibrary("koin.android").get())
                "implementation"(libs.findLibrary("koin.androidx.viewmodel").get())
                "ksp"(
                    libs.findLibrary("koin.compiler").get(),
                )
            }
        }
    }
}
