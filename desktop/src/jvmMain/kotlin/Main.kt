import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.mifospay.shared.MainView

fun main() {
    application {
        val windowState = rememberWindowState()
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "MifosWallet"
        ) {
            MainView()
        }
    }
}
