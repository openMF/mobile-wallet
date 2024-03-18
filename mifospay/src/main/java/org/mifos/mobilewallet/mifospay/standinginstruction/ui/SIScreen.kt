package org.mifos.mobilewallet.mifospay.standinginstruction.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingAccount
import com.mifos.mobilewallet.model.entity.client.Client
import com.mifos.mobilewallet.model.entity.client.Status
import com.mifos.mobilewallet.model.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.SIUiState
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.StandingInstructionsViewModel
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen

@Composable
fun SIScreen(
    viewModel: StandingInstructionsViewModel = hiltViewModel(),
    onItemClicked:(Long) -> Unit,
    onNewSIClick:() -> Unit
){
    val siState by viewModel.siState.collectAsStateWithLifecycle()
    SIScreen(
        siState = siState,
        onItemClicked = onItemClicked,
        onNewSIClick = onNewSIClick
    )
}

@Composable
fun SIScreen(
    siState: SIUiState,
    onItemClicked: (Long) -> Unit,
    onNewSIClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ){
        when (siState) {
            SIUiState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.loading),
                    backgroundColor = Color.White
                )
            }

            is SIUiState.Empty -> {
                NoSIAddSIScreen(onNewSIClick)
            }

            is SIUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.error_no_si_found),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is SIUiState.SIList -> {
                SIScreenContent(
                    standingInstructionsList = siState.standingInstructions,
                    onItemClicked = onItemClicked,
                    onNewSIClick = onNewSIClick
                )
            }
        }
    }
}

@Composable
fun SIScreenContent(
    standingInstructionsList:List<StandingInstruction>,
    onItemClicked:(Long) -> Unit,
    onNewSIClick:() -> Unit
){
    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewSIClick.invoke() },
                modifier = Modifier .padding(horizontal = 10.dp, vertical = 60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_white),
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    ){  contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ){
            SIList(
                list =standingInstructionsList,
                onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
fun SIList(
    list:List<StandingInstruction>,
    onItemClicked:(Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(list) {
            SIListItem(
                fromClientName = it.fromClient.displayName.toString(),
                toClientName = it.toClient.displayName.toString(),
                validTill = it.validTill.toString(),
                amount = it.amount.toString(),
                id = it.id,
                onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
fun SIListItem(
    fromClientName: String,
    toClientName: String,
    validTill: String,
    amount: String,
    id:Long,
    onItemClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(colorResource(R.color.grey_100))
                .padding(16.dp)
                .clickable { (onItemClicked.invoke(id)) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = fromClientName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = toClientName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = validTill,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
            Text(
                text = stringResource(R.string.rupee_symbol) + amount,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

        }
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
}

@Composable
fun NoSIAddSIScreen(onNewSIClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.add_si))
        FloatingActionButton(
            onClick = { onNewSIClick.invoke() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 10.dp, vertical = 60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_white),
                contentDescription = stringResource(R.string.add),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SIWithSampleDataPreview(){
    MifosTheme {
        SIScreen(
            siState = SIUiState.SIList(dummyStandingInstructions),
            onItemClicked = {},
            onNewSIClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SIEmptyPreview(){
    MifosTheme {
        SIScreen(
            siState = SIUiState.Empty,
            onItemClicked = {},
            onNewSIClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SIErrorPreview(){
    MifosTheme {
        SIScreen(
            siState = SIUiState.Error,
            onItemClicked = {},
            onNewSIClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SILoadingPreview(){
    MifosTheme {
        SIScreen(
            siState = SIUiState.Loading,
            onItemClicked = {},
            onNewSIClick = {}
        )
    }
}

val dummyStandingInstructions = listOf(
    StandingInstruction(
        id = 1,
        name = "Monthly Rent",
        fromClient = Client(1, "123456",status = null,null, listOf(null), listOf(null),"x","y","z","XYZ"),
        fromAccount = SavingAccount(2, "1234567890"),
        toClient = Client(1, "987654", status = null,null, listOf(null), listOf(null),"x","y","z","XYZ"),
        toAccount = SavingAccount(3, "0987654321"),
        status = Status(1),
        amount = 1000.0,
        validFrom = listOf(2023, 5, 1),
        validTill = listOf(2024, 4, 30),
        recurrenceInterval = 30,
        recurrenceOnMonthDay = listOf(1)
    ),
    StandingInstruction(
        id = 2,
        name = "Utility Bill",
        fromClient = Client(2, "234567",status = null,null, listOf(null), listOf(null),"x","y","z","ABC"),
        fromAccount = SavingAccount(2, "2345678901"),
        toClient = Client(2, "876543",status = null,null, listOf(null), listOf(null),"x","y","z","ABC"),
        toAccount = SavingAccount(2, "1098765432"),
        status = Status(2,null,null),
        amount = 150.75,
        validFrom = listOf(2023, 4, 15),
        validTill = listOf(2024, 4, 14),
        recurrenceInterval = 30,
        recurrenceOnMonthDay = listOf(15)
    )
)

