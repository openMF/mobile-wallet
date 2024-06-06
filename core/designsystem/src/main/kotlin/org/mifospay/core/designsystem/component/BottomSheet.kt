package org.mifospay.core.designsystem.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosBottomSheet(
    content: @Composable () -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(true) }


    fun dismissSheet() {
        coroutineScope.launch { modalSheetState.hide() }.invokeOnCompletion {
            if (!modalSheetState.isVisible) {
                showBottomSheet = false
            }
        }
        onDismiss.invoke()
    }

    BackHandler(modalSheetState.isVisible) {
        dismissSheet()
    }

    AnimatedVisibility(visible = showBottomSheet) {
        ModalBottomSheet(
            containerColor = Color.White,
            onDismissRequest = {
                showBottomSheet = false
                dismissSheet()
            },
            sheetState = modalSheetState
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun MifosBottomSheetPreview() {
    MifosBottomSheet({
        Box {
            Modifier.height(100.dp)
        }
    }, {})
}