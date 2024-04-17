package org.mifospay.core.ui.utility

import androidx.compose.runtime.Composable

data class TabContent(
    val tabName: String,
    val content: @Composable () -> Unit
)

