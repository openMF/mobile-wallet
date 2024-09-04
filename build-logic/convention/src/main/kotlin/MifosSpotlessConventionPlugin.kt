import org.gradle.api.Plugin
import org.gradle.api.Project
import org.mifospay.configureSpotless
import org.mifospay.spotlessGradle

class MifosSpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugins()

            spotlessGradle {
                configureSpotless(this)
            }
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            apply("com.diffplug.spotless")
        }
    }
}