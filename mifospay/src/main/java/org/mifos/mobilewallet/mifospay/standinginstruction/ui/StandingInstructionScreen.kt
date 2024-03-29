package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosLoadingWheel
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionViewModel
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionsUiState
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen

@Composable
fun StandingInstructionsScreen(
    viewModel: StandingInstructionViewModel = hiltViewModel(),
    onNewSI: () -> Unit
) {
    val standingInstructionsUiState by viewModel.standingInstructionsUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = viewModel::refreshSI
    ){
        StandingInstructionScreen(
            standingInstructionsUiState = standingInstructionsUiState,
            onNewSI = onNewSI
        )
    }

}

@Composable
fun StandingInstructionScreen(
    standingInstructionsUiState: StandingInstructionsUiState,
    onNewSI: () -> Unit,
) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            when (standingInstructionsUiState) {
                StandingInstructionsUiState.Empty -> {
                    EmptyContentScreen(
                        modifier = Modifier,
                        title = stringResource(id = R.string.error_oops),
                        subTitle = stringResource(id = R.string.empty_standing_instructions),
                        iconTint = Color.Black,
                        iconImageVector = Icons.Rounded.Info
                    )
                }

                is StandingInstructionsUiState.Error -> {
                    EmptyContentScreen(
                        modifier = Modifier,
                        title = stringResource(id = R.string.error_oops),
                        subTitle = stringResource(id = R.string.error_fetching_si_list),
                        iconTint = Color.Black,
                        iconImageVector = Icons.Rounded.Info
                    )
                }

                StandingInstructionsUiState.Loading -> {
                    MifosLoadingWheel(
                        modifier = Modifier.fillMaxWidth(),
                        contentDesc = stringResource(R.string.loading)
                    )
                }

                is StandingInstructionsUiState.StandingInstructionList -> {
                    Scaffold(
                        modifier = Modifier,
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { onNewSI.invoke() },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_add_white),
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(standingInstructionsUiState.standingInstructionList) { items ->
                                    SIContent(
                                        fromClientName = items.fromClient.displayName.toString(),
                                        toClientName = items.toClient.displayName.toString(),
                                        validTill = items.validTill.toString(),
                                        amount = items.amount.toString(),
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun StandingInstructionsScreenLoadingPreview() {
    StandingInstructionScreen(
        standingInstructionsUiState = StandingInstructionsUiState.Loading,
        onNewSI = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun StandingInstructionsEmptyPreview() {
    StandingInstructionScreen(
        standingInstructionsUiState = StandingInstructionsUiState.Empty,
        onNewSI = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun StandingInstructionsErrorPreview() {
    StandingInstructionScreen(
        standingInstructionsUiState = StandingInstructionsUiState.Error("Error Screen"),
        onNewSI = {}
    )
}