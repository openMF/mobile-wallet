package org.mifos.mobilewallet.mifospay.settings.ui

import MifosDialogBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosItemCard
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosTopBar
import org.mifos.mobilewallet.mifospay.designsystem.theme.mifosText
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleSettingsButton

@Composable
fun SettingsScreen(
    backPress: () -> Unit,
    disable: () -> Unit,
    logout: () -> Unit
) {
    var currentDialog by remember { mutableStateOf(DialogType.NONE) }

    Scaffold(
        topBar = {
            MifosTopBar(topBarTitle = R.string.settings, backPress = { backPress.invoke() })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
        ) {
            Row(modifier = Modifier.padding(20.dp)) {
                MifosItemCard(
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.notification_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 17.sp, color = mifosText)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {}

            Row(modifier = Modifier.padding(8.dp)) {
                MifosItemCard(
                    onClick = { currentDialog = DialogType.DISABLE_ACCOUNT },
                    colors = CardDefaults.cardColors(Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.disable_account).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = styleSettingsButton
                    )
                }
            }

            Row(modifier = Modifier.padding(8.dp)) {
                MifosItemCard(
                    onClick = { currentDialog = DialogType.LOGOUT },
                    colors = CardDefaults.cardColors(Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = styleSettingsButton
                    )
                }
            }
        }
    }

    if (currentDialog != DialogType.NONE) {
        MifosDialogBox(
            showDialog = true,
            onDismiss = { currentDialog = DialogType.NONE },
            title = when (currentDialog) {
                DialogType.DISABLE_ACCOUNT -> R.string.alert_disable_account
                DialogType.LOGOUT -> R.string.log_out_title
                else -> R.string.empty
            },
            message = when (currentDialog) {
                DialogType.DISABLE_ACCOUNT -> R.string.alert_disable_account_desc
                else -> R.string.empty
            },
            confirmButtonText = when (currentDialog) {
                DialogType.DISABLE_ACCOUNT -> R.string.ok
                DialogType.LOGOUT -> R.string.yes
                else -> R.string.empty
            },
            dismissButtonText = when (currentDialog) {
                DialogType.DISABLE_ACCOUNT -> R.string.cancel
                DialogType.LOGOUT -> R.string.no
                else -> R.string.empty
            },
            onConfirm = {
                when (currentDialog) {
                    DialogType.DISABLE_ACCOUNT -> disable()
                    DialogType.LOGOUT -> logout()
                    else -> {}
                }
                currentDialog = DialogType.NONE
            }
        )
    }

}

@Preview(showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen({}, {}, {})
}