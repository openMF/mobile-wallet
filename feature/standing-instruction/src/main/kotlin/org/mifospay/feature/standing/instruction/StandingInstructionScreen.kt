package org.mifospay.feature.standing.instruction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.accounts.savings.SavingAccount
import com.mifospay.core.model.entity.client.Client
import com.mifospay.core.model.entity.client.Status
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.EmptyContentScreen

@Composable
fun StandingInstructionsScreen(
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
) = when (standingInstructionsUiState) {
    StandingInstructionsUiState.Empty -> {
        EmptyContentScreen(
            modifier = Modifier,
            title = stringResource(id = R.string.feature_standing_instruction_error_oops),
            subTitle = stringResource(id = R.string.feature_standing_instruction_empty_standing_instructions),
            iconTint = Color.Black,
            iconImageVector = Icons.Rounded.Info
        )
    }

    is StandingInstructionsUiState.Error -> {
        EmptyContentScreen(
            modifier = Modifier,
            title = stringResource(id = R.string.feature_standing_instruction_error_oops),
            subTitle = stringResource(id = R.string.feature_standing_instruction_error_fetching_si_list),
            iconTint = Color.Black,
            iconImageVector = Icons.Rounded.Info
        )
    }

    StandingInstructionsUiState.Loading -> {
        MifosLoadingWheel(
            modifier = Modifier.fillMaxWidth(),
            contentDesc = stringResource(R.string.feature_standing_instruction_loading)
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
                        painter = rememberVectorPainter(MifosIcons.Add),
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

class StandingInstructionPreviewParameterProvider : PreviewParameterProvider<StandingInstructionsUiState> {
    override val values: Sequence<StandingInstructionsUiState> = sequenceOf(
        StandingInstructionsUiState.Loading,
        StandingInstructionsUiState.Empty,
        StandingInstructionsUiState.Error("Error Screen"),
        StandingInstructionsUiState.StandingInstructionList(
            standingInstructionList = listOf(
                StandingInstruction(
                    id = 1,
                    name = "Instruction 1",
                    fromClient = Client(displayName = "Alice"),
                    fromAccount = SavingAccount(),
                    toClient = Client(displayName = "Bob"),
                    toAccount = SavingAccount(),
                    status = Status(),
                    amount = 100.0,
                    validFrom = listOf(2022, 1, 1),
                    validTill = listOf(2024, 12, 31),
                    recurrenceInterval = 30,
                    recurrenceOnMonthDay = listOf(1)
                ),
                StandingInstruction(
                    id = 2,
                    name = "Instruction 2",
                    fromClient = Client(displayName = "Charlie"),
                    fromAccount = SavingAccount(),
                    toClient = Client(displayName = "Dave"),
                    toAccount = SavingAccount(),
                    status = Status(),
                    amount = 200.0,
                    validFrom = listOf(2022, 1, 1),
                    validTill = listOf(2024, 12, 31),
                    recurrenceInterval = 30,
                    recurrenceOnMonthDay = listOf(1)
                )
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun StandingInstructionsScreenPreview(
    @PreviewParameter(StandingInstructionPreviewParameterProvider::class) standingInstructionsUiState: StandingInstructionsUiState
) {
    StandingInstructionScreen(
        standingInstructionsUiState = standingInstructionsUiState,
        onNewSI = {}
    )
}