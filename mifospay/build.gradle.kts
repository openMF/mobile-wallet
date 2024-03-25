import org.mifos.mobilewallet.mifospay.MifosBuildType

plugins {
    alias(libs.plugins.mifospay.android.application)
    alias(libs.plugins.mifospay.android.application.compose)
   // alias(libs.plugins.mifospay.android.application.flavors)
    alias(libs.plugins.mifospay.android.hilt)
    alias(libs.plugins.mifospay.android.application.firebase)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.roborazzi)
}

apply(from = "../config/quality/quality.gradle")

android {
    namespace = "org.mifos.mobilewallet.mifospay"
    defaultConfig {
        applicationId = "org.mifos.mobilewallet.mifospay"
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
           // applicationIdSuffix = MifosBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
           // applicationIdSuffix = MifosBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.androidx.constraintlayout)

    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation("androidx.vectordrawable:vectordrawable-animated:1.1.0")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    api("com.google.android.material:material:1.0.0") // update require alot of UI changes

    implementation(projects.feature.auth)
    implementation(projects.feature.passcode)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation("androidx.compose.ui:ui-graphics")

    // we need it for country picker library
    implementation("androidx.compose.material:material:1.6.0")
    implementation(libs.compose.country.code.picker) // remove after moving auth code to module

    //calender for date picking
    implementation(libs.sheets.compose.dialogs.core)
    implementation(libs.sheets.compose.dialogs.calender)

    // ViewModel
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation(libs.jakewharton.butterknife)
    implementation(libs.jakewharton.compiler)

    // Splash API
    implementation("androidx.core:core-splashscreen:1.0.1")

    runtimeOnly(libs.androidx.compose.runtime)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.squareup.retrofit2) {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude(module = "okhttp")
    }
    implementation(libs.squareup.retrofit.adapter.rxjava)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.logging.interceptor)

    implementation("com.github.barteksc:android-pdf-viewer:2.8.2")

    implementation(libs.reactivex.rxjava.android)
    implementation(libs.reactivex.rxjava)

    implementation("io.michaelrocks:libphonenumber-android:8.11.0")

    implementation("me.dm7.barcodescanner:zxing:1.9.13")
    implementation("com.journeyapps:zxing-android-embedded:4.2.0")

    implementation("com.mifos.mobile:mifos-passcode:0.3.0")

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.guava:guava:27.0.1-android")

    implementation("com.hbb20:ccp:2.2.0")
    implementation("com.github.MdFarhanRaja:SearchableSpinner:1.9")
    implementation("com.alimuzaffar.lib:pinentryedittext:1.3.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.yalantis:ucrop:2.2.2")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation(libs.junit)
}
