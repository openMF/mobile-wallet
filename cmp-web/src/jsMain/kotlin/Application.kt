
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.skiko.wasm.onWasmReady
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()

    onWasmReady {
        ComposeViewport(document.body!!) {
            MifosPaySharedApp(
                handleAppLocale = { languageTag ->
                    if (languageTag != null) {
                        // Store language preference in localStorage
                        localStorage.setItem("app_language", languageTag)
                        // Set HTML lang attribute for accessibility
                        document.documentElement?.setAttribute("lang", languageTag)
                    } else {
                        // System Default: remove stored language preference
                        localStorage.removeItem("app_language")
                        // Reset to browser's default language
                        val browserLang = window.navigator.language
                        document.documentElement?.setAttribute("lang", browserLang)
                    }
                    // Reload page to apply language changes (required for web)
                    // Note: This will reload the page, and locale selection depends on browser settings
                    // window.location.reload()
                },
            )
        }
    }
}