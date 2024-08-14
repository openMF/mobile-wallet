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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.accounts.savings.SavingAccount
import com.mifospay.core.model.entity.client.Client
import com.mifospay.core.model.entity.client.Status
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.designsystem.component.FloatingActionButtonContent
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons

@Composable
internal fun SIDetailsScreen(
    onClickCreateNew: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StandingInstructionDetailsViewModel = hiltViewModel(),
) {
    val siDetailUiState by viewModel.siDetailsUiState.collectAsStateWithLifecycle()

    SIDetailsScreen(
        siDetailsUiState = siDetailUiState,
        onClickCreateNew = onClickCreateNew,
        onBackPress = onBackPress,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun SIDetailsScreen(
    siDetailsUiState: SiDetailsUiState,
    onClickCreateNew: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val floatingActionButtonContent = FloatingActionButtonContent(
        onClick = onClickCreateNew,
        contentColor = Color.Black,
        content = {
            androidx.compose.material3.Icon(
                imageVector = MifosIcons.Edit,
                contentDescription = stringResource(R.string.feature_standing_instruction_downloading_receipt),
            )
        },
    )

    MifosScaffold(
        topBarTitle = R.string.feature_standing_instruction_details,
        floatingActionButtonContent = floatingActionButtonContent,
        backPress = onBackPress,
        modifier = modifier,
        scaffoldContent = {
            when (siDetailsUiState) {
                SiDetailsUiState.Loading -> MifosLoadingWheel(
                    modifier = Modifier.fillMaxWidth(),
                    contentDesc = stringResource(R.string.feature_standing_instruction_loading),
                )

                is SiDetailsUiState.ShowSiDetails -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        SIDetailsContent(
                            paddingValues = it,
                            siName = siDetailsUiState.standingInstruction.name,
                            siId = siDetailsUiState.standingInstruction.id,
                            amount = siDetailsUiState.standingInstruction.amount,
                            validFrom = siDetailsUiState.standingInstruction.validFrom,
                            validTill = siDetailsUiState.standingInstruction.validTill
                                ?: emptyList(),
                            siToName = siDetailsUiState.standingInstruction.toClient.displayName
                                ?: "",
                            siFromNumber = siDetailsUiState.standingInstruction.fromClient.mobileNo,
                            siToNumber = siDetailsUiState.standingInstruction.toClient.mobileNo,
                            recurrenceInterval = siDetailsUiState.standingInstruction.recurrenceInterval,
                            status = siDetailsUiState.standingInstruction.status.value ?: "",
                            siFromName = siDetailsUiState.standingInstruction.fromAccount.clientName
                                ?: "",
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun SIDetailsContent(
    paddingValues: PaddingValues,
    siName: String,
    siId: Long,
    amount: Double,
    validFrom: List<Int>,
    validTill: List<Int>,
    siToName: String,
    siToNumber: String,
    siFromName: String,
    siFromNumber: String,
    recurrenceInterval: Int,
    status: String,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = siName,
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Text(
            text = stringResource(R.string.feature_standing_instruction_standing_instruction_id),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            text = siId.toString(),
            fontSize = 16.sp,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_standing_instruction_amount),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = amount.toString(),
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp),
            )
        }

        Text(
            text = stringResource(R.string.feature_standing_instruction_valid_from),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Text(
            text = validFrom.toString(),
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_standing_instruction_valid_till),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = validTill.toString(),
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 16.dp),
            )
        }

        Text(
            text = stringResource(R.string.feature_standing_instruction_to),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            text = siToName,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = siToNumber,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = stringResource(R.string.feature_standing_instruction_from),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            text = siFromName,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = siFromNumber,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.feature_standing_instruction_interval),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = recurrenceInterval.toString(),
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 16.dp),
            )
        }

        Text(
            text = stringResource(R.string.feature_standing_instruction_status),
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            text = status,
            fontSize = 16.sp,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSIDetailsScreen() {
    SIDetailsScreen(
        siDetailsUiState = SiDetailsUiState.ShowSiDetails(
            StandingInstruction(
                id = 1,
                name = "Dummy Standing Instruction",
                fromClient = Client(),
                fromAccount = SavingAccount(),
                toClient = Client(),
                toAccount = SavingAccount(),
                status = Status(),
                amount = 1000.0,
                validFrom = listOf(2024, 1, 1),
                validTill = listOf(2025, 12, 31),
                recurrenceInterval = 1,
                recurrenceOnMonthDay = listOf(1),
            ),
        ),
        onBackPress = {},
        onClickCreateNew = {},
    )
}
