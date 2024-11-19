import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.compose.resources.configureWebResources
import org.mifospay.shared.MifosPaySharedApp
import org.mifospay.shared.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()

    configureWebResources {
        resourcePathMapping { path -> "./$path" }
    }

    CanvasBasedWindow(
        title = "MifosWallet",
        canvasElementId = "ComposeTarget",
    ) {
        MifosPaySharedApp()
    }
}