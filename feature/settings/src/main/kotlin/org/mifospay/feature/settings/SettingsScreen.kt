/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType

@Composable
fun SettingsScreenRoute(
    backPress: () -> Unit,
    navigateToEditPasswordScreen: () -> Unit,
    onLogout: () -> Unit,
    onChangePasscode: () -> Unit,
    modifier: Modifier = Modifier,
    viewmodel: SettingsViewModel = hiltViewModel(),
) {
    var dialogState by remember { mutableStateOf(DialogState()) }
    val paddingValues = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)

    DialogManager(
        dialogState = dialogState,
        onDismiss = { dialogState = DialogState(type = DialogType.NONE) },
    )

    Scaffold(
        topBar = {
            MifosTopBar(
                topBarTitle = R.string.feature_settings_settings,
                backPress = { backPress.invoke() },
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(contentPadding),
        ) {
            Row(
                modifier = Modifier.padding(paddingValues),
            ) {
                MifosCard(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_notification_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(paddingValues),
            ) {
                MifosCard(
                    onClick = { onChangePasswordClicked(navigateToEditPasswordScreen) },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_change_password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(paddingValues),
            ) {
                MifosCard(
                    onClick = onChangePasscode,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_change_passcode),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {}

            Row(modifier = Modifier.padding(8.dp)) {
                MifosCard(
                    onClick = {
                        dialogState = DialogState(
                            type = DialogType.DISABLE_ACCOUNT,
                            onConfirm = { viewmodel.disableAccount() },
                        )
                    },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.error),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_disable_account).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onError,
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
                                onLogout()
                            },
                        )
                    },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_settings_log_out).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }
        }
    }
}

private fun onChangePasswordClicked(navigateToEditPasswordScreen: () -> Unit) {
    navigateToEditPasswordScreen.invoke()
}

@Preview(showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenRoute(
        backPress = {},
        navigateToEditPasswordScreen = {},
        onLogout = {},
        onChangePasscode = {},
        viewmodel = hiltViewModel(),
    )
}
