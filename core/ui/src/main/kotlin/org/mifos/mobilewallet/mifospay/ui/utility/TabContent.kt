package org.mifos.mobilewallet.mifospay.ui.utility

import androidx.compose.runtime.Composable

data class TabContent(
    val tabName: String,
    val content: @Composable () -> Unit
)

