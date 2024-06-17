package org.mifospay.feature.settings

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.theme.mifosText
import org.mifospay.core.designsystem.theme.styleSettingsButton
import org.mifospay.feature.auth.login.LoginActivity

import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType
import org.mifospay.settings.R

/**
 * @author pratyush
 * @since 12/02/2024
 */

@Composable
fun SettingsScreen(
    viewmodel: SettingsViewModel = hiltViewModel(),
    backPress: () -> Unit,
    onChangePassword: () -> Unit,
    onChangePasscode: () -> Unit
) {
    val context = LocalContext.current
    var dialogState by remember { mutableStateOf(DialogState()) }

    DialogManager(
        dialogState = dialogState,
        onDismiss = { dialogState = DialogState(type = DialogType.NONE) }
    )

    Scaffold(
        topBar = {
            MifosTopBar(
                topBarTitle = R.string.feature_settings_settings,
                backPress = { backPress.invoke() })
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(contentPadding)
        ) {
            Row(
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 4.dp
                )
            ) {
                MifosCard(
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_notification_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 18.sp, color = mifosText)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 4.dp
                    )
            ) {
                MifosCard(
                    onClick = { onChangePassword.invoke() },
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_change_password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 18.sp, color = mifosText)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 4.dp
                    )
            ) {
                MifosCard(
                    onClick = { onChangePasscode.invoke() },
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_change_passcode),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 18.sp, color = mifosText)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {}

            Row(modifier = Modifier.padding(8.dp)) {
                MifosCard(
                    onClick = {
                        dialogState = DialogState(
                            type = DialogType.DISABLE_ACCOUNT,
                            onConfirm = { viewmodel.disableAccount() }
                        )
                    },
                    colors = CardDefaults.cardColors(Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_disable_account).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = styleSettingsButton
                    )
                }
            }

            Row(modifier = Modifier.padding(8.dp)) {
                MifosCard(
                    onClick = {
                        dialogState = DialogState(
                            type = DialogType.LOGOUT,
                            onConfirm = {
                                viewmodel.logout()
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                )
                                context.startActivity(intent)
                            }
                        )
                    },
                    colors = CardDefaults.cardColors(Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_log_out).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = styleSettingsButton
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(hiltViewModel(), {}, {}, {})
}