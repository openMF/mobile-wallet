/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.FloatingActionButtonContent
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.ui.EmptyContentScreen

@Composable
fun StandingInstructionsScreenRoute(
    onNewSI: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StandingInstructionViewModel = koinViewModel(),
) {
    val standingInstructionsUiState by viewModel.standingInstructionsUiState.collectAsStateWithLifecycle()

    StandingInstructionScreen(
        standingInstructionsUiState = standingInstructionsUiState,
        onNewSI = onNewSI,
        onBackPress = onBackPress,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun StandingInstructionScreen(
    standingInstructionsUiState: StandingInstructionsUiState,
    onNewSI: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val floatingActionButtonContent = FloatingActionButtonContent(
        onClick = onNewSI,
        contentColor = NewUi.gradientOne,
        content = {
            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = stringResource(R.string.feature_standing_instruction_downloading_receipt),
            )
        },
    )

    MifosScaffold(
        backPress = onBackPress,
        floatingActionButtonContent = floatingActionButtonContent,
        modifier = modifier,
        scaffoldContent = {},
    ) {
        when (standingInstructionsUiState) {
            StandingInstructionsUiState.Empty -> {
                EmptyContentScreen(
                    title = stringResource(id = R.string.feature_standing_instruction_error_oops),
                    subTitle = stringResource(id = R.string.feature_standing_instruction_empty_standing_instructions),
                    modifier = Modifier,
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconImageVector = MifosIcons.Info,
                )
            }

            is StandingInstructionsUiState.Error -> {
                EmptyContentScreen(
                    title = stringResource(id = R.string.feature_standing_instruction_error_oops),
                    subTitle = stringResource(id = R.string.feature_standing_instruction_error_fetching_si_list),
                    modifier = Modifier,
                    iconTint = MaterialTheme.colorScheme.primary,
                    iconImageVector = MifosIcons.RoundedInfo,
                )
            }

            StandingInstructionsUiState.Loading -> {
                MifosLoadingWheel(
                    modifier = Modifier.fillMaxWidth(),
                    contentDesc = stringResource(R.string.feature_standing_instruction_loading),
                )
            }

            is StandingInstructionsUiState.StandingInstructionList -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
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
private fun StandingInstructionsScreenLoadingPreview() {
    StandingInstructionScreen(
        standingInstructionsUiState = StandingInstructionsUiState.Loading,
        onNewSI = {},
        onBackPress = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun StandingInstructionsEmptyPreview() {
    StandingInstructionScreen(
        standingInstructionsUiState = StandingInstructionsUiState.Empty,
        onNewSI = {},
        onBackPress = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun StandingInstructionsErrorPreview() {
    StandingInstructionScreen(
        standingInstructionsUiState = StandingInstructionsUiState.Error("Error Screen"),
        onNewSI = {},
        onBackPress = {},
    )
}
