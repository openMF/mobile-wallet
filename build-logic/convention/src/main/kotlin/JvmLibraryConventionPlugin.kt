import org.gradle.api.Plugin
import org.gradle.api.Project
import org.mifospay.configureKotlinJvm

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("mifospay.android.lint")
            }
            configureKotlinJvm()
        }
    }
}
