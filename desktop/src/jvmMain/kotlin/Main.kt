import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.mifospay.shared.App

fun main() {
    application {
        Window(
            title = "Kmp App",
            onCloseRequest = ::exitApplication
        ) {
           App()
        }
    }
}