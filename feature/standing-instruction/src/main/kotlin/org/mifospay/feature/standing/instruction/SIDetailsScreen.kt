package org.mifospay.feature.standing.instruction

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
fun SIDetailsScreen(
    viewModel: StandingInstructionDetailsViewModel = hiltViewModel(),
    onBackPress: () -> Unit
) {
    val siDetailUiState by viewModel.siDetailsUiState.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()
    val deleteSuccess by viewModel.deleteSuccess.collectAsStateWithLifecycle()

    SIDetailsScreen(
        siDetailsUiState = siDetailUiState,
        onBackPress = onBackPress
    )
}

@Composable
fun SIDetailsScreen(
    siDetailsUiState: SiDetailsUiState,
    onBackPress: () -> Unit
) {
    val floatingActionButtonContent = FloatingActionButtonContent(
        onClick = { },
        contentColor = Color.Black,
        content = {
            androidx.compose.material3.Icon(
                imageVector = MifosIcons.Edit,
                contentDescription = stringResource(R.string.feature_standing_instruction_downloading_receipt)
            )
        }
    )

    MifosScaffold(
        topBarTitle = R.string.feature_standing_instruction_details,
        floatingActionButtonContent = floatingActionButtonContent,
        backPress = {},
        scaffoldContent = {
            when (siDetailsUiState) {
                SiDetailsUiState.Loading -> MifosLoadingWheel(
                    modifier = Modifier.fillMaxWidth(),
                    contentDesc = stringResource(R.string.feature_standing_instruction_loading)
                )

                is SiDetailsUiState.ShowSiDetails -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        SIDetailsContent(
                            paddingValues = it,
                            siName = "Pratyush",
                            siId = "1234567890",
                            amount = "1000",
                            validFrom = "1/1/11",
                            validTill = "1/2/11",
                            siToName = "Aditya",
                            siFromNumber = "9898989898",
                            siToNumber = "99990088665",
                            recurrenceInterval = 3,
                            status = "Active",
                            siFromName = "Pratyush"
                        )
                    }

                }
            }
        }
    ) {

    }
}

@Composable
fun SIDetailsContent(
    paddingValues: PaddingValues,
    siName: String,
    siId: String,
    amount: String,
    validFrom: String,
    validTill: String,
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
            .fillMaxWidth()
    ) {
        Text(
            text = siName,
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Standing Instruction ID
        Text(
            text = "Standing Instruction ID:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = siId, // Replace with actual data
            fontSize = 16.sp,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Amount
        Text(
            text = "Amount:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = amount, // Replace with actual data
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp)
            )
            // TextInputLayout for editing amount (hidden by default)
        }

        // Valid From
        Text(
            text = "Valid From:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = validFrom, // Replace with actual data
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Valid Till
        Text(
            text = "Valid Till:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = validTill, // Replace with actual data
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 16.dp)
            )
            // Text for "Pick the date" (hidden by default)
        }

        // To Name and Number
        Text(
            text = "To:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = siToName,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = siToNumber, // Replace with actual data
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // From Name and Number
        Text(
            text = "From:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = siFromName,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = siFromNumber, // Replace with actual data
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Recurrence Interval
        Text(
            text = "Recurrence Interval in Months:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = recurrenceInterval.toString(), // Replace with actual data
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 16.dp)
            )
            // TextInputLayout for editing interval (hidden by default)
        }

        // Status
        Text(
            text = "Status:",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = status, // Replace with actual data
            fontSize = 16.sp,
            color = Color.Blue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSIDetailsScreen() {
    SIDetailsScreen(siDetailsUiState = SiDetailsUiState.ShowSiDetails(
        StandingInstruction(
            id = 1, // Replace with actual ID
            name = "Dummy Standing Instruction",
            fromClient = Client(),
            fromAccount = SavingAccount(),
            toClient = Client(),
            toAccount = SavingAccount(),
            status = Status(),
            amount = 1000.0, // Replace with actual amount
            validFrom = listOf(2024, 1, 1), // Replace with actual date
            validTill = listOf(2025, 12, 31), // Replace with actual date
            recurrenceInterval = 1, // Replace with actual interval
            recurrenceOnMonthDay = listOf(1) // Replace with actual recurrence day
        )
    ), onBackPress = {})
}
