import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

class KMPLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.multiplatform")
        target.plugins.apply("org.jetbrains.dokka")

        target.tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
            outputDirectory.set(target.layout.buildDirectory.dir("dokka"))
            dokkaSourceSets.configureEach {
                if (name == "commonMain" || name == "androidMain") {
                    includeNonPublic.set(true)
                    skipDeprecated.set(true)
                    reportUndocumented.set(true)
                    jdkVersion.set(8)
                }
            }
        }

        target.extensions.configure(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java) {
            // Add your existing KMP configuration here
        }
    }
}