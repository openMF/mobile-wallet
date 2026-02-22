package org.mifospay.feature.auth.chooseAuthOption.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cmp.sample.shared.chooseAuthOption.DialogButton

@Composable
fun MessageDialogBox(
    onDismiss: () -> Unit,
    onButtonClick: () -> Unit,
    dialogMessage: String,
    confirmButtonText: String,
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(16.dp),
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    dialogMessage,
                    modifier = Modifier
                        .padding(8.dp),
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))
                DialogButton(
                    onClick = {
                        onButtonClick
                    },
                    modifier = Modifier
                        .padding(end = 8.dp),
                    text = confirmButtonText,
                )
            }
        }
    }
}
