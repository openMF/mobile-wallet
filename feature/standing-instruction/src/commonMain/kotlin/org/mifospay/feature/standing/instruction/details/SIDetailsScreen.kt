/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.standing_instruction.generated.resources.Res
import mobile_wallet.feature.standing_instruction.generated.resources.feature_standing_instruction_error_oops
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.common.DateHelper
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.standing.instruction.components.FrequencyChip
import org.mifospay.feature.standing.instruction.components.InstructionTypeChip
import org.mifospay.feature.standing.instruction.components.PriorityChip

@Composable
internal fun SIDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SIDetailViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            SIDEvent.OnNavigateBack -> navigateBack.invoke()
        }
    }

    SIDetailsScreen(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
internal fun SIDetailsScreen(
    state: SIDetailState,
    modifier: Modifier = Modifier,
    onAction: (SIDAction) -> Unit,
) {
    MifosScaffold(
        topBarTitle = "Instruction Details",
        modifier = modifier,
        backPress = {
            onAction(SIDAction.NavigateBack)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state.viewState) {
                is ViewState.Loading -> {
                    MifosLoadingWheel(contentDesc = "Loading")
                }

                is ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_standing_instruction_error_oops),
                        subTitle = state.viewState.message,
                    )
                }

                is ViewState.Content -> {
                    SIDetailsContent(
                        state = state.viewState,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun SIDetailsContent(
    state: ViewState.Content,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        item {
            SIDetailsCard(
                item = state.data,
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun SIDetailsCard(
    item: StandingInstruction,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                RowBlock {
                    Text(text = "Instruction Name")
                    Text(text = item.name, fontWeight = FontWeight.SemiBold)
                }

                val amount = CurrencyFormatter.format(
                    balance = item.amount,
                    currencyCode = "USD",
                    maximumFractionDigits = 2,
                )

                RowBlock {
                    Text(text = "Instruction Amount")
                    Text(text = amount, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "Status")
                    Text(text = item.status.value, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "Transfer Type")
                    Text(text = item.transferType.value, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "Priority")

                    PriorityChip(
                        priority = item.priority,
                    )
                }

                RowBlock {
                    Text(text = "Instruction Type")
                    InstructionTypeChip(
                        type = item.instructionType,
                    )
                }

                RowBlock {
                    Text(text = "Recurrence Frequency")

                    FrequencyChip(
                        option = item.recurrenceFrequency,
                        interval = item.recurrenceInterval.toString(),
                    )
                }

                RowBlock {
                    val validFrom = DateHelper.getDateAsString(item.validFrom)

                    Text(text = "Valid From")
                    Text(text = validFrom, fontWeight = FontWeight.SemiBold)
                }

                RowBlock(
                    showDivider = item.recurrenceOnMonthDay.isNotEmpty(),
                ) {
                    val validTill = DateHelper.getDateAsString(item.validTill)

                    Text(text = "Valid Till")
                    Text(text = validTill, fontWeight = FontWeight.SemiBold)
                }

                if (item.recurrenceOnMonthDay.isNotEmpty()) {
                    val recurrenceOnMonthDay =
                        DateHelper.getDateMonthString(item.recurrenceOnMonthDay)

                    RowBlock(false) {
                        Text(text = "Recurrence On Month Day")
                        Text(text = recurrenceOnMonthDay, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                RowBlock {
                    Text(text = "From Office")
                    Text(text = item.fromOffice.name, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "From Client")
                    Text(text = item.fromClient.displayName, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "From Account Type")
                    Text(text = item.fromAccountType.value, fontWeight = FontWeight.SemiBold)
                }

                RowBlock(false) {
                    Text(text = "From Account")
                    Text(text = item.fromAccount.accountNo, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                RowBlock {
                    Text(text = "To Office")
                    Text(text = item.toOffice.name, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "To Client")
                    Text(text = item.toClient.displayName, fontWeight = FontWeight.SemiBold)
                }

                RowBlock {
                    Text(text = "To Account Type")
                    Text(text = item.toAccountType.value, fontWeight = FontWeight.SemiBold)
                }

                RowBlock(false) {
                    Text(text = "To Account")
                    Text(text = item.toAccount.accountNo, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private inline fun RowBlock(
    showDivider: Boolean = true,
    crossinline content: @Composable (RowScope.() -> Unit),
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }

        if (showDivider) {
            MifosDivider()
        }
    }
}
