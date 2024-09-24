import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.mifospay.libs


class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            dependencies {
                val bom = libs.findLibrary("koin-bom").get()
                add("implementation", platform(bom))
                add("implementation", libs.findLibrary("koin.core").get())

                add("implementation", libs.findLibrary("koin.annotations").get())
                add("ksp", libs.findLibrary("koin.ksp.compiler").get())


                add("testImplementation", libs.findLibrary("koin.test").get())
                add("testImplementation", libs.findLibrary("koin.test.junit4").get())
            }

            extensions.configure<KspExtension> {
                arg("KOIN_CONFIG_CHECK","true")
                arg("USE_COMPOSE_VIEWMODEL", "false")
                arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
            }
        }
    }
}
