package org.mifospay.shared

import androidx.compose.ui.window.ComposeUIViewController
import org.mifospay.shared.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}