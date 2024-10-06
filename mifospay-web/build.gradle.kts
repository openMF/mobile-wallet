import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

val copyJsResources = tasks.create("copyJsResourcesWorkaround", Copy::class.java) {
    from(project(":mifospay-shared").file("src/commonMain/composeResources"))
    into("build/processedResources/js/main")
}

val copyWasmResources = tasks.create("copyWasmResourcesWorkaround", Copy::class.java) {
    from(project(":mifospay-shared").file("src/commonMain/composeResources"))
    into("build/processedResources/wasmJs/main")
}

afterEvaluate {
    project.tasks.getByName("jsProcessResources").finalizedBy(copyJsResources)
    project.tasks.getByName("wasmJsProcessResources").finalizedBy(copyWasmResources)
//    project.tasks.getByName("jsBrowserProductionExecutableDistributeResources").mustRunAfter(copyJsResources)
    project.tasks.getByName("jsDevelopmentExecutableCompileSync").mustRunAfter(copyJsResources)
    project.tasks.getByName("wasmJsDevelopmentExecutableCompileSync").mustRunAfter(copyWasmResources)
    project.tasks.getByName("jsProductionExecutableCompileSync").mustRunAfter(copyJsResources)
    project.tasks.getByName("wasmJsProductionExecutableCompileSync").mustRunAfter(copyWasmResources)
}


kotlin {
    js(IR) {
        moduleName = "mifospay-web"
        browser {
            commonWebpackConfig {
                outputFileName = "mifospay-web.js"
            }
        }
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "mifospay-web"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {

                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/mifospay-shared/")
                        add(project.rootDir.path + "/nonAndroidMain/")
                        add(project.rootDir.path + "/mifospay-web/")
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsWasmMain by creating {
            dependencies {
                implementation(project(":mifospay-shared"))
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        jsMain {
            dependencies {
                dependsOn(jsWasmMain)
            }
        }

        val wasmJsMain by getting {
            dependsOn(jsWasmMain)
        }
    }
}