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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.ui.utility.DialogState
import org.mifospay.core.ui.utility.DialogType

@Composable
fun SettingsScreenRoute(
    backPress: () -> Unit,
    navigateToEditPasswordScreen: () -> Unit,
    onLogout: () -> Unit,
    onChangePasscode: () -> Unit,
    navigateToFaqScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewmodel: SettingsViewModel = koinViewModel(),
) {
    var dialogState by remember { mutableStateOf(DialogState()) }

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
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsCardItem(
                title = stringResource(id = R.string.feature_settings_notification_settings),
            )

            SettingsCardItem(
                title = stringResource(id = R.string.feature_settings_faq),
                onClick = navigateToFaqScreen,
            )

            SettingsCardItem(
                title = stringResource(id = R.string.feature_settings_change_password),
                onClick = navigateToEditPasswordScreen,
            )

            SettingsCardItem(
                title = stringResource(id = R.string.feature_settings_change_passcode),
                onClick = onChangePasscode,
            )

            SettingsCardItem(
                title = stringResource(id = R.string.feature_settings_log_out),
                onClick = {
                    dialogState = DialogState(
                        type = DialogType.LOGOUT,
                        onConfirm = {
                            viewmodel.logout()
                            onLogout()
                        },
                    )
                },
            )

            SettingsCardItem(
                title = stringResource(id = R.string.feature_settings_disable_account),
                color = Color.Red,
                onClick = {
                    dialogState = DialogState(
                        type = DialogType.DISABLE_ACCOUNT,
                        onConfirm = { viewmodel.disableAccount() },
                    )
                },
                hasHorizontalDivider = false,
            )
        }
    }
}

@Composable
fun SettingsCardItem(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    hasHorizontalDivider: Boolean = true,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Text(
            text = title,
            fontWeight = FontWeight(400),
            modifier = Modifier.padding(20.dp),
            color = color,
        )

        if (hasHorizontalDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenRoute(
        backPress = {},
        navigateToEditPasswordScreen = {},
        onLogout = {},
        onChangePasscode = {},
        viewmodel = koinViewModel(),
        navigateToFaqScreen = { },
    )
}
