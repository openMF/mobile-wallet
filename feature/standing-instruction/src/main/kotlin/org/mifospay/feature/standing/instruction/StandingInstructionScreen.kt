package org.mifospay.feature.standing.instruction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifospay.core.designsystem.component.FloatingActionButtonContent
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.EmptyContentScreen

@Composable
fun StandingInstructionsScreenRoute(
    viewModel: StandingInstructionViewModel = hiltViewModel(),
    onNewSI: () -> Unit
) {
    val standingInstructionsUiState by viewModel.standingInstructionsUiState.collectAsStateWithLifecycle()
    StandingInstructionScreen(
        standingInstructionsUiState = standingInstructionsUiState,
        onNewSI = onNewSI
    )
}

@Composable
fun StandingInstructionScreen(
    standingInstructionsUiState: StandingInstructionsUiState,
    onNewSI: () -> Unit
) {

    val floatingActionButtonContent = FloatingActionButtonContent(
        onClick = { onNewSI.invoke() },
        contentColor = MaterialTheme.colorScheme.primary,
        content = {
            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = stringResource(R.string.feature_standing_instruction_downloading_receipt)
            )
        }
    )

    MifosScaffold(
        backPress = { /*TODO*/ },
        floatingActionButtonContent = floatingActionButtonContent,
        scaffoldContent = {

        }) {
        when (standingInstructionsUiState) {
            StandingInstructionsUiState.Empty -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.feature_standing_instruction_error_oops),
                    subTitle = stringResource(id = R.string.feature_standing_instruction_empty_standing_instructions, ),
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is StandingInstructionsUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.feature_standing_instruction_error_oops),
                    subTitle = stringResource(id = R.string.feature_standing_instruction_error_fetching_si_list),
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconImageVector = MifosIcons.RoundedInfo
                )
            }

            StandingInstructionsUiState.Loading -> {
                MifosLoadingWheel(
                    modifier = Modifier.fillMaxWidth(),
                    contentDesc = stringResource(R.string.feature_standing_instruction_loading)
                )
            }

            is StandingInstructionsUiState.StandingInstructionList -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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