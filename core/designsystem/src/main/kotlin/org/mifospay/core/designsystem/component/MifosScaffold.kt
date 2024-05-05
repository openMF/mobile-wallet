package org.mifospay.core.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosScaffold(
    topBarTitle: Int,
    backPress: () -> Unit,
    onFloatingActionButtonClick: () -> Unit,
    floatingActionButtonContentColor: Color,
    floatingActionButtonContent:@Composable () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                { MifosTopBar(
                    topBarTitle,backPress
                ) }
            )
                 },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFloatingActionButtonClick,
                contentColor = floatingActionButtonContentColor ,
                content = floatingActionButtonContent
            )
        },
        snackbarHost = snackbarHost,
        content = scaffoldContent
    )
}