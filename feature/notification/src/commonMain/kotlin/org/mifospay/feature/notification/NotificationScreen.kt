/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.notification

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.notification.generated.resources.Res
import mobile_wallet.feature.notification.generated.resources.feature_notification_error_oops
import mobile_wallet.feature.notification.generated.resources.feature_notification_loading
import mobile_wallet.feature.notification.generated.resources.feature_notification_nothing_to_notify
import mobile_wallet.feature.notification.generated.resources.feature_notification_notifications
import mobile_wallet.feature.notification.generated.resources.feature_notification_there_is_nothing_to_show
import mobile_wallet.feature.notification.generated.resources.feature_notification_unexpected_error_subtitle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.model.notification.Notification
import org.mifospay.core.ui.EmptyContentScreen

@Composable
internal fun NotificationScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewmodel: NotificationViewModel = koinViewModel(),
) {
    val uiState by viewmodel.notificationUiState.collectAsStateWithLifecycle()

    NotificationScreen(
        uiState = uiState,
        modifier = modifier,
        navigateBack = navigateBack,
    )
}

@Composable
@VisibleForTesting
internal fun NotificationScreen(
    uiState: NotificationUiState,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
) {
    MifosScaffold(
        topBarTitle = stringResource(Res.string.feature_notification_notifications),
        backPress = navigateBack,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState) {
                is NotificationUiState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_notification_error_oops),
                        subTitle = stringResource(Res.string.feature_notification_unexpected_error_subtitle),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.primary,
                    )
                }

                is NotificationUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_notification_loading),
                        modifier = Modifier.align(Alignment.Center),
                        backgroundColor = Color.Transparent,
                    )
                }

                is NotificationUiState.Success -> {
                    if (uiState.notificationList.isEmpty()) {
                        EmptyContentScreen(
                            title = stringResource(Res.string.feature_notification_nothing_to_notify),
                            subTitle = stringResource(Res.string.feature_notification_there_is_nothing_to_show),
                            modifier = Modifier,
                            iconTint = MaterialTheme.colorScheme.onSurface,
                        )
                    } else {
                        NotificationScreenContent(state = uiState)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationScreenContent(
    state: NotificationUiState.Success,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = state.notificationList,
            key = {
                it.id
            },
        ) { notification ->
            NotificationListItem(
                notification = notification,
            )
        }
    }
}

@Composable
private fun NotificationListItem(
    notification: Notification,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(text = notification.content)
            },
            trailingContent = {
                Text(text = notification.formattedDate)
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

internal class NotificationUiStateProvider : PreviewParameterProvider<NotificationUiState> {
    override val values: Sequence<NotificationUiState>
        get() = sequenceOf(
            NotificationUiState.Success(sampleNotificationList),
            NotificationUiState.Error("Error Occurred"),
            NotificationUiState.Loading,
        )
}

@Preview
@Composable
private fun NotificationScreenPreview(
    @PreviewParameter(NotificationUiStateProvider::class)
    notificationUiState: NotificationUiState,
) {
    MifosTheme {
        NotificationScreen(
            uiState = notificationUiState,
            navigateBack = {},
        )
    }
}

internal val sampleNotificationList = List(10) {
    Notification(
        id = it.toLong(),
        objectType = "duo",
        objectId = 9851,
        action = "nisl",
        actorId = 9344,
        content = "non",
        isRead = false,
        isSystemGenerated = false,
        createdAt = "bibendum",
    )
}
