package org.mifospay.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class FloatingActionButtonContent(
    val onClick: (() -> Unit),
    val contentColor: Color,
    val content: (@Composable () -> Unit)
)

@Composable
fun MifosScaffold(
    topBarTitle: Int? = null,
    backPress: () -> Unit,
    floatingActionButtonContent: FloatingActionButtonContent? = null,
    snackbarHost: @Composable () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Scaffold(
        topBar = {
            if (topBarTitle != null) {
                MifosTopBar(
                    topBarTitle = topBarTitle,
                    backPress = backPress,
                    actions = actions
                )
            }
        },
        floatingActionButton = {
            floatingActionButtonContent?.let { content ->
                FloatingActionButton(
                    onClick = content.onClick,
                    contentColor = content.contentColor,
                    content = content.content
                )
            }
        },
        snackbarHost = snackbarHost,
        content = scaffoldContent,
    )
}