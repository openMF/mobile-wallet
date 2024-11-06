
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()

    ComposeViewport(document.body!!) {
        MifosPaySharedApp()
    }
}