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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.settings.generated.resources.Res
import mobile_wallet.feature.settings.generated.resources.feature_settings_change_passcode
import mobile_wallet.feature.settings.generated.resources.feature_settings_change_password
import mobile_wallet.feature.settings.generated.resources.feature_settings_disable_account
import mobile_wallet.feature.settings.generated.resources.feature_settings_faq
import mobile_wallet.feature.settings.generated.resources.feature_settings_log_out
import mobile_wallet.feature.settings.generated.resources.feature_settings_notification_settings
import mobile_wallet.feature.settings.generated.resources.feature_settings_settings
import mobile_wallet.feature.settings.generated.resources.outline_logout
import mobile_wallet.feature.settings.generated.resources.outline_password
import mobile_wallet.feature.settings.generated.resources.outline_pin
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun SettingsScreenRoute(
    backPress: () -> Unit,
    onEditPassword: () -> Unit,
    onLogout: () -> Unit,
    onChangePasscode: () -> Unit,
    navigateToFaqScreen: () -> Unit,
    navigateToNotificationScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewmodel: SettingsViewModel = koinViewModel(),
) {
    val state by viewmodel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewmodel) { event ->
        when (event) {
            SettingsEvent.OnNavigateBack -> backPress.invoke()
            SettingsEvent.OnNavigateToChangePasscodeScreen -> onChangePasscode.invoke()
            SettingsEvent.OnNavigateToEditPasswordScreen -> onEditPassword.invoke()
            SettingsEvent.OnNavigateToFaqScreen -> navigateToFaqScreen.invoke()
            SettingsEvent.OnNavigateToLogout -> onLogout.invoke()
            SettingsEvent.OnNavigateToNotificationScreen -> navigateToNotificationScreen.invoke()
        }
    }

    Box(modifier) {
        SettingsDialogs(
            dialogState = state.dialogState,
            onDismissRequest = remember(viewmodel) {
                { viewmodel.trySendAction(SettingsAction.DismissDialog) }
            },
        )

        SettingsScreenContent(
            modifier = Modifier,
            onAction = viewmodel::trySendAction,
        )
    }
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    onAction: (SettingsAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = stringResource(Res.string.feature_settings_settings),
        backPress = {
            onAction(SettingsAction.NavigateBack)
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingsCardItem(
                title = stringResource(Res.string.feature_settings_notification_settings),
                icon = MifosIcons.OutlinedNotifications,
                onClick = {
                    onAction(SettingsAction.NavigateToNotificationSettings)
                },
            )

            SettingsCardItem(
                title = stringResource(Res.string.feature_settings_faq),
                icon = MifosIcons.OutlinedInfo,
                onClick = {
                    onAction(SettingsAction.NavigateToFaqScreen)
                },
            )

            SettingsCardItem(
                title = stringResource(Res.string.feature_settings_change_password),
                icon = vectorResource(Res.drawable.outline_password),
                onClick = {
                    onAction(SettingsAction.ChangePassword)
                },
            )

            SettingsCardItem(
                title = stringResource(Res.string.feature_settings_change_passcode),
                icon = vectorResource(Res.drawable.outline_pin),
                onClick = {
                    onAction(SettingsAction.ChangePasscode)
                },
            )

            SettingsCardItem(
                title = stringResource(Res.string.feature_settings_log_out),
                icon = vectorResource(Res.drawable.outline_logout),
                onClick = {
                    onAction(SettingsAction.Logout)
                },
            )

            SettingsCardItem(
                title = stringResource(Res.string.feature_settings_disable_account),
                icon = MifosIcons.OutlinedLock,
                color = Color.Red,
                onClick = {
                    onAction(SettingsAction.DisableAccount)
                },
            )
        }
    }
}

@Composable
private fun SettingsCardItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = NewUi.onSurface,
    onClick: () -> Unit,
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = title,
            )
        },
        headlineContent = {
            Text(text = title)
        },
        trailingContent = {
            Icon(
                imageVector = MifosIcons.ChevronRight,
                contentDescription = null,
            )
        },
        modifier = modifier.clickable {
            onClick()
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = color,
        ),
    )
}

@Composable
private fun SettingsDialogs(
    dialogState: DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        is DialogState.DisableAccount -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = stringResource(dialogState.title),
                message = stringResource(dialogState.message),
            ),
            onConfirm = dialogState.onConfirm,
            onDismissRequest = onDismissRequest,
        )

        is DialogState.Logout -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = stringResource(dialogState.title),
                message = stringResource(dialogState.message),
            ),
            onConfirm = dialogState.onConfirm,
            onDismissRequest = onDismissRequest,
        )

        null -> Unit
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenContent(
        onAction = {},
    )
}
