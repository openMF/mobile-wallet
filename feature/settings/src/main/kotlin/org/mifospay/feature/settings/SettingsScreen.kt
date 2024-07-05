package org.mifospay.feature.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.theme.mifosText
import org.mifospay.core.designsystem.theme.styleSettingsButton
import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType
import org.mifospay.feature.auth.login.LoginActivity
import org.mifospay.feature.editpassword.EditPasswordActivity
import org.mifospay.feature.passcode.PassCodeActivity

/**
 * @author pratyush
 * @since 12/02/2024
 */

@Composable
fun SettingsScreenRoute(
    viewmodel: SettingsViewModel = hiltViewModel(),
    backPress: () -> Unit,
) {
    val context = LocalContext.current
    var dialogState by remember { mutableStateOf(DialogState()) }
    val passcodePreferencesHelper = viewmodel.passcodePreferencesHelper

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
                .background(MaterialTheme.colorScheme.surface)
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
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_notification_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
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
                    onClick = { onChangePasswordClicked(context) },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_change_password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
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
                    onClick = { onChangePasscodeClicked(context, passcodePreferencesHelper) },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_change_passcode),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
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
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_disable_account).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onError
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
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_log_out).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

fun onChangePasswordClicked(context: Context) {
    context.startActivity(Intent(context, EditPasswordActivity::class.java))
}

fun onChangePasscodeClicked(
    context: Context,
    passcodePreferencesHelper: PasscodePreferencesHelper
) {
    val currentPasscode = passcodePreferencesHelper.passCode
    passcodePreferencesHelper.savePassCode("")
    PassCodeActivity.startPassCodeActivity(
        context = context,
        bundle = bundleOf(
            Pair(Constants.CURRENT_PASSCODE, currentPasscode),
            Pair(Constants.UPDATE_PASSCODE, true)
        )
    )
}

@Preview(showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreenRoute(hiltViewModel(), {})
}