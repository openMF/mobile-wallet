@file:OptIn(ExperimentalSettingsApi::class)

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.mifospay.core.model.LanguageConfig
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.di.initKoin

private fun setLocaleOverride(localeCode: String) {
    window.localStorage.setItem("__mifos_locale_override", localeCode)
}

private fun applyLocaleOverride() {
    js(
        """
        (function() {
            var loc = localStorage.getItem('__mifos_locale_override');
            if (loc) {
                Object.defineProperty(navigator, 'language', {
                    get: function() { return loc; },
                    configurable: true
                });
                Object.defineProperty(navigator, 'languages', {
                    get: function() { return [loc]; },
                    configurable: true
                });
            }
        })();
        """
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val settings = Settings()
    val languageConfig = settings.decodeValueOrNull(
        key = "language",
        serializer = LanguageConfig.serializer(),
    )
    val injectedLocale = languageConfig?.localName

    if (injectedLocale != null) {
        setLocaleOverride(injectedLocale)
        applyLocaleOverride()
    }

    initKoin()

    onWasmReady {
        ComposeViewport(document.body!!) {
            MifosPaySharedApp(
                onLanguageChange = { languageCode ->
                    if (languageCode != injectedLocale) {
                        window.location.reload()
                    }
                },
            )
        }
    }
}