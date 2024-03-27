package org.mifos.mobilewallet.mifospay.bank.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosBottomSheet
import org.mifos.mobilewallet.mifospay.theme.stylePrimaryBlueMedium24sp
import org.mifos.mobilewallet.mifospay.theme.stylePrimaryBlueSmall14sp


@Composable
fun ChooseSimDialogSheet(
    onSimSelected: (Int) -> Unit
) {
    MifosBottomSheet(content = {
        ChooseSimDialogSheetContent(onSimSelected)
    }, onDismiss = {
        onSimSelected.invoke(0)
    })
}

@Composable
fun ChooseSimDialogSheetContent(onSimSelected: (Int) -> Unit) {
    var selectedSim by remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.verify_mobile_number),
            style = stylePrimaryBlueMedium24sp
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = stringResource(id = R.string.confirm_mobile_number_message),
            style = stylePrimaryBlueSmall14sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.bank_account_mobile_verification_conditions))
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SimCard(
                simNumber = 1,
                isSelected = selectedSim == 1,
                onSimSelected = { selectedSim = 1 })

            Spacer(modifier = Modifier.width(24.dp))
            Text(text = stringResource(id = R.string.or))
            Spacer(modifier = Modifier.width(24.dp))
            SimCard(
                simNumber = 2,
                isSelected = selectedSim == 2,
                onSimSelected = { selectedSim = 2 })
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(id = R.string.regular_charges_will_apply), color = Color.Black)
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = { onSimSelected(selectedSim) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(250.dp)
                .clip(CircleShape)
        ) {
            Text(text = stringResource(id = R.string.confirm))
        }
    }
}

@Composable
fun SimCard(
    simNumber: Int, isSelected: Boolean, onSimSelected: () -> Unit
) {
    val drawable: Painter =
        painterResource(id = if (isSelected) R.drawable.sim_card_selected else R.drawable.sim_card_unselected)
    Image(painter = drawable,
        contentDescription = "SIM Card $simNumber",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(50.dp)
            .clickable { onSimSelected() })
}

@Preview
@Composable
fun SimSelectionPreview() {
    Surface(color = Color.White) {
        ChooseSimDialogSheet(onSimSelected = {})
    }
}
