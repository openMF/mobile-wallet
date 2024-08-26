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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.library.pullrefresh.PullRefreshIndicator
import com.mifos.library.pullrefresh.pullRefresh
import com.mifos.library.pullrefresh.rememberPullRefreshState
import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.notification.R

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    viewmodel: NotificationViewModel = hiltViewModel(),
) {
    val uiState by viewmodel.notificationUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewmodel.isRefreshing.collectAsStateWithLifecycle()
    NotificationScreen(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onRefresh = viewmodel::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
internal fun NotificationScreen(
    uiState: NotificationUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
    Box(modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MifosTopAppBar(titleRes = R.string.feature_notification_notifications)
            when (uiState) {
                is NotificationUiState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(id = R.string.feature_notification_error_oops),
                        subTitle = stringResource(id = R.string.feature_notification_unexpected_error_subtitle),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconImageVector = MifosIcons.RoundedInfo,
                    )
                }

                NotificationUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.feature_notification_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is NotificationUiState.Success -> {
                    if (uiState.notificationList.isEmpty()) {
                        EmptyContentScreen(
                            title = stringResource(R.string.feature_notification_nothing_to_notify),
                            subTitle = stringResource(R.string.feature_notification_there_is_nothing_to_show),
                            modifier = Modifier,
                            iconTint = MaterialTheme.colorScheme.onSurface,
                            iconImageVector = MifosIcons.RoundedInfo,
                        )
                    } else {
                        LazyColumn {
                            items(uiState.notificationList) { notification ->
                                NotificationListItem(
                                    title = notification.title.toString(),
                                    body = notification.body.toString(),
                                    timestamp = notification.timestamp.toString(),
                                )
                            }
                        }
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun NotificationListItem(
    title: String,
    body: String,
    timestamp: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
        }
    }
}

internal class NotificationUiStateProvider :
    PreviewParameterProvider<NotificationUiState> {
    override val values: Sequence<NotificationUiState>
        get() = sequenceOf(
            NotificationUiState.Success(sampleNotificationList),
            NotificationUiState.Error("Error Occurred"),
            NotificationUiState.Loading,
        )
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview(
    @PreviewParameter(NotificationUiStateProvider::class)
    notificationUiState: NotificationUiState,
) {
    MifosTheme {
        NotificationScreen(
            uiState = notificationUiState,
            isRefreshing = false,
            onRefresh = {},
        )
    }
}

internal val sampleNotificationList = List(10) {
    NotificationPayload("Title", "Body", "TimeStamp")
}
