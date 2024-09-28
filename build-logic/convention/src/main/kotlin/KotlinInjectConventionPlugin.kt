import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.mifospay.libs

class KotlinInjectConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }
            dependencies {
                add("kspCommonMainMetadata", libs.findLibrary("kotlin.inject.compiler.ksp").get())
                add("commonMainImplementation", libs.findLibrary("kotlin.inject.runtime.kmp").get())
                // KSP will eventually have better multiplatform support and we'll be able to simply have
                // `ksp libs.kotlinInject.compiler` in the dependencies block of each source set
                // https://github.com/google/ksp/pull/1021
                add("kspIosX64", libs.findLibrary("kotlin.inject.compiler.ksp").get())
                add("kspIosArm64", libs.findLibrary("kotlin.inject.compiler.ksp").get())
                add("kspIosSimulatorArm64", libs.findLibrary("kotlin.inject.compiler.ksp").get())
//                add("kspWasmJs", libs.findLibrary("kotlin.inject.compiler.ksp").get())
                add("kspAndroid", libs.findLibrary("kotlin.inject.compiler.ksp").get())
                add("kspJvm", libs.findLibrary("kotlin.inject.compiler.ksp").get())
            }
        }
    }
}