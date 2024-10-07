import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()

    CanvasBasedWindow(
        title = "MifosWallet",
        canvasElementId = "root",
    ) {
        MifosPaySharedApp()
    }
}