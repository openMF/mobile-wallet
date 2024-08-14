package org.mifospay.shared

import androidx.compose.runtime.Composable

@Composable
fun MainView() = App()

class JVMPlatform: Platform {
    override val name: String ="Windows"
}

actual fun getPlatform(): Platform = JVMPlatform()