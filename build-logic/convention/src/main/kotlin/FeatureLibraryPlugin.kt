import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaTask

class FeatureLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.withType(DokkaTask::class.java).configureEach {
            outputDirectory.set(target.layout.buildDirectory.dir("dokka"))
            moduleName.set("feature")
            dokkaSourceSets.configureEach {
                sourceLink {
                    localDirectory.set(target.file("src"))
                    remoteUrl.set(java.net.URI("https://github.com/openMF/mobile-wallet.git").toURL())
                    remoteLineSuffix.set("#L")
                }
            }
        }
    }
}