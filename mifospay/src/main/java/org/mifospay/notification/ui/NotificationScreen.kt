package org.mifospay.notification.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifospay.core.model.domain.NotificationPayload
import org.mifospay.R
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosTopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    uiState: NotificationUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh
    ) {
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
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_notification_found),
                                style = styleMedium16sp
                            )
                        }
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
                style = styleMedium16sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = body, style = styleMedium16sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Text(
                text = timestamp, style = historyItemTextStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun NotificationLoadingPreview() {
    NotificationScreen(uiState = NotificationUiState.Loading, isRefreshing = true, onRefresh = {})
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun NotificationSuccessPreview() {
    NotificationScreen(
        uiState = NotificationUiState.Success(sampleNotificationList),
        isRefreshing = true,
        onRefresh = {})
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun NotificationErrorScreenPreview() {
    NotificationScreen(
        uiState = NotificationUiState.Error("Error Occurred"),
        isRefreshing = true,
        onRefresh = {}
    )
}

val sampleNotificationList = List(10) {
    NotificationPayload("Title", "Body", "TimeStamp")
}