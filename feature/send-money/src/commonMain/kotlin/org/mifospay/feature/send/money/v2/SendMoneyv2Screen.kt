package org.mifospay.feature.send.money.v2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_scan_qr
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.MifosSearchBar
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.send.money.SendMoneyAction
import v2.SendMoneyV2Action
import v2.SendMoneyV2Event
import v2.SendMoneyV2ViewModel

@Composable
fun SendMoneyv2Screen(
    navigateToSelectAccountScreen: () -> Unit,
    navigateBack:()->Unit,
    modifier: Modifier = Modifier,
    viewModel: SendMoneyV2ViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when(event){
            SendMoneyV2Event.NavigateToSearchAccountSelection -> navigateToSelectAccountScreen()

            SendMoneyV2Event.NavigateBack -> navigateBack()
        }
    }

    SendMoneyScreen(
        showTopBar = true,
        modifier = Modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SendMoneyScreen(
    showTopBar: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (SendMoneyV2Action) -> Unit,
) {
    MifosGradientBackground {
        MifosScaffold(
            modifier = modifier,
            topBar = {
                AnimatedVisibility(
                    visible = showTopBar,
                ) {
                    MifosTopBar(
                        topBarTitle = stringResource(Res.string.feature_send_money_send),
                        backPress = {
                            onAction(SendMoneyV2Action.NavigateBack)
                        },
                    )
                }
            },
            bottomBar = {},
        ) { paddingValues ->
            Column(
                Modifier.padding(paddingValues)
            ) {
                MifosSearchBar(
                    query = "",
                    placeHolder = "",
                    onQueryChange = {},
                    onSearch = {},
                    onClick = {
                        onAction(SendMoneyV2Action.OnSearchBarClicked)
                    },
                    enabled = false
                )
            }
        }
    }
}