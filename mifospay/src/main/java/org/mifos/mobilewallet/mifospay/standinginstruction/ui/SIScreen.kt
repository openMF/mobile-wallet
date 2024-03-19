package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosErrorLayout
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosLoadingWheel
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionsUiState

@Composable
fun SIScreen(
    standingInstructionsUiState: StandingInstructionsUiState,
    onNewSI: () -> Unit
) = when (standingInstructionsUiState) {
    StandingInstructionsUiState.EmptyState -> {
        MifosErrorLayout(
            modifier = Modifier.fillMaxSize(),
            icon = R.drawable.ic_empty_state,
            error = R.string.empty_standing_instructions
        )
    }

    is StandingInstructionsUiState.Error -> {
        MifosErrorLayout(
            modifier = Modifier.fillMaxSize(),
            icon = R.drawable.ic_empty_state,
            error = R.string.error_fetching_si_list
        )
    }

    StandingInstructionsUiState.Initial -> {}

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

@Preview
@Composable
fun SIScreenPreview() {
    SIScreen(standingInstructionsUiState = StandingInstructionsUiState.EmptyState, {})
}