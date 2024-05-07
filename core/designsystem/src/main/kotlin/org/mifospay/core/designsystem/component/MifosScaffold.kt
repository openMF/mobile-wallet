package org.mifospay.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class FloatingActionButtonContent(
    val onClick: (() -> Unit)? = null,
    val contentColor: Color? = null,
    val content: (@Composable () -> Unit)? = null
)

@Composable
fun MifosScaffold(
    topBarTitle: Int,
    backPress: () -> Unit,
    floatingActionButtonContent: FloatingActionButtonContent? = null,
    snackbarHost: @Composable () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            MifosTopBar(
                topBarTitle, backPress
            )
        },
        floatingActionButton = {
            floatingActionButtonContent?.let { content ->
                FloatingActionButton(
                    onClick = content.onClick ?: {},
                    contentColor = content.contentColor ?: Color.Cyan,
                    content = content.content ?: {}
                )
            }
        },
        snackbarHost = snackbarHost,
        content = scaffoldContent
    )
}