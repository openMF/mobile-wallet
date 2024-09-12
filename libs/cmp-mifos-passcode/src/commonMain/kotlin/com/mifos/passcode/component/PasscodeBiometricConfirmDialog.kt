package com.mifos.passcode.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mifos.passcode.resources.Res
import com.mifos.passcode.resources.enable_biometric_dialog_description
import com.mifos.passcode.resources.enable_biometric_dialog_title
import com.mifos.passcode.resources.no
import com.mifos.passcode.resources.yes
import com.mifos.passcode.theme.blueTint
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasscodeBiometricConfirmDialog(
    cancelBiometric: () -> Unit,
    setBiometric: () -> Unit
) {

    Dialog(onDismissRequest = { cancelBiometric.invoke() }) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {

            Column {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(resource = Res.string.enable_biometric_dialog_title),
                    modifier = Modifier
                        .padding(8.dp),
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(resource = Res.string.enable_biometric_dialog_description),
                    modifier = Modifier
                        .padding(8.dp),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    DialogButton(
                        onClick = { cancelBiometric.invoke() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f),
                        text = stringResource(resource = Res.string.no)
                    )

                    DialogButton(
                        onClick = { setBiometric.invoke() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        text = stringResource(resource = Res.string.yes)
                    )
                }
            }
        }
    }
}

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = blueTint,
            contentColor = White,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = White
        )
    ) {
        Text(text = text)
    }
}