package org.mifospay.notification.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.historyItemTextStyle
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.notification.presenter.NotificationUiState
import org.mifospay.notification.presenter.NotificationViewModel

@Composable
fun NotificationScreen(viewmodel: NotificationViewModel = hiltViewModel()) {
    val uiState by viewmodel.notificationUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewmodel.isRefreshing.collectAsStateWithLifecycle()
    NotificationScreen(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewmodel.refresh()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MifosTopAppBar(titleRes = R.string.notifications)
            when (uiState) {
                is NotificationUiState.Error -> {
                    EmptyContentScreen(
                        modifier = Modifier,
                        title = stringResource(id = R.string.error_oops),
                        subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                        iconTint = Color.Black,
                        iconImageVector = Icons.Rounded.Info
                    )
                }

                NotificationUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.loading),
                        backgroundColor = Color.White
                    )
                }

                is NotificationUiState.Success -> {
                    if (uiState.notificationList.isEmpty()) {
                        EmptyContentScreen(
                            modifier = Modifier,
                            title = stringResource(id = R.string.error_oops),
                            subTitle = stringResource(id = R.string.no_notification_found),
                            iconTint = Color.Black,
                            iconImageVector = Icons.Rounded.Info
                        )
                    } else {
                        LazyColumn {
                            items(uiState.notificationList) { notification ->
                                NotificationList(
                                    title = notification.title.toString(),
                                    body = notification.body.toString(),
                                    timestamp = notification.timestamp.toString()
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
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun NotificationList(title: String, body: String, timestamp: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = body, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = timestamp, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

class NotificationUiStateProvider :
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
fun NotificationScreenPreview(
    @PreviewParameter(NotificationUiStateProvider::class) notificationUiState: NotificationUiState
) {
    MifosTheme {
        NotificationScreen(
            uiState = notificationUiState,
            isRefreshing = false,
            onRefresh = {}
        )
    }
}

val sampleNotificationList = List(10) {
    NotificationPayload("Title", "Body", "TimeStamp")
}