/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.android.app

import android.content.res.Resources
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cmp.shared.SharedApp
import io.github.mobilebytelabs.kmptoolkit.firebase.analytics.AnalyticsHelper
import io.github.mobilebytelabs.kmptoolkit.firebase.analytics.AppLifecycleTracker
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kpt.core.base.data.infra.NetworkMonitor
import kpt.core.base.platform.update.AppUpdateManager
import kpt.core.base.platform.update.AppUpdateManagerImpl
import kpt.core.base.ui.util.ShareUtils
import kpt.core.data.user.UserDataRepository
import org.koin.android.ext.android.inject
import java.util.Locale

/**
 * Main activity class. This class is used to set the content view of the
 * activity.
 *
 * @constructor Create empty Main activity
 * @see ComponentActivity
 */
@Suppress("UnusedPrivateProperty")
class MainActivity : AppCompatActivity() {

    private lateinit var appUpdateManager: AppUpdateManager

    private val userPreferencesRepository: UserDataRepository by inject()

    private val networkMonitor: NetworkMonitor by inject()

    private val analyticsHelper: AnalyticsHelper by inject()
    private val lifecycleTracker by lazy { AppLifecycleTracker(analyticsHelper) }

    override fun onCreate(savedInstanceState: Bundle?) {
        var shouldShowSplashScreen = true
        installSplashScreen().setKeepOnScreenCondition { shouldShowSplashScreen }

        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerImpl(this)

        val darkThemeConfigFlow = userPreferencesRepository.observeDarkThemeConfig

        setupEdgeToEdge(darkThemeConfigFlow)

        ShareUtils.setActivityProvider { return@setActivityProvider this }
        FileKit.init(this)

        analyticsHelper.setUserId(deviceData)

        setContent {
            val status by networkMonitor.isOnline.collectAsStateWithLifecycle(false)

            if (status) {
                appUpdateManager.checkForAppUpdate()
            }

            lifecycleTracker.markAppLaunchComplete()

            SharedApp(
                updateScreenCapture = ::updateScreenCapture,
                handleRecreate = ::handleRecreate,
                handleThemeMode = {
                    AppCompatDelegate.setDefaultNightMode(it)
                },
                handleAppLocale = { localeTag ->
                    val currentLocales = AppCompatDelegate.getApplicationLocales()
                    val newLocales = if (localeTag != null) {
                        LocaleListCompat.forLanguageTags(localeTag)
                    } else {
                        // System Default: clear app-specific locale
                        LocaleListCompat.getEmptyLocaleList()
                    }

                    // Only update if the locale has actually changed
                    if (currentLocales != newLocales) {
                        AppCompatDelegate.setApplicationLocales(newLocales)
                        // Update Locale.setDefault for non-UI formatting
                        if (localeTag != null) {
                            // Use forLanguageTag to properly parse locales like "en-GB", "pt-BR"
                            Locale.setDefault(Locale.forLanguageTag(localeTag))
                        } else {
                            // Reset to true system default locale from device configuration
                            // Use Resources.getSystem() to get device locale unaffected by app overrides
                            val systemLocale = Resources.getSystem().configuration.locales[0]
                            Locale.setDefault(systemLocale)
                        }
                    }
                },
                onSplashScreenRemoved = {
                    shouldShowSplashScreen = false
                },
            )
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.checkForResumeUpdateState()
        lifecycleTracker.onEnterBackground()
    }

    override fun onStart() {
        super.onStart()
        lifecycleTracker.markAppLaunchStart()
    }

    private fun handleRecreate() {
        recreate()
    }

    private fun updateScreenCapture(isScreenCaptureAllowed: Boolean) {
        // Debug builds always allow screen capture so QA / device-test tooling (adb
        // screencap, screen recordings, Android Studio Profiler captures) work without
        // toggling the user-facing "Allow screen capture" preference. Release builds
        // honor the user preference — FLAG_SECURE is on by default for production
        // because this is a fintech app (loan balances, EMI amounts, account names).
        val allow = isScreenCaptureAllowed || BuildConfig.DEBUG
        if (allow) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
