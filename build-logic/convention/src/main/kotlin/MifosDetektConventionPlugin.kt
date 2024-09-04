
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.mifospay.configureDetekt
import org.mifospay.detektGradle

class MifosDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()

            detektGradle {
                configureDetekt(this)
            }
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply("io.gitlab.arturbosch.detekt")
        }
    }
}