package org.mifospay.feature.bank.accounts.choose.sim

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.bank.accounts.R

@Composable
fun ChooseSimDialogSheet(
    onSimSelected: (Int) -> Unit
) {
    MifosBottomSheet(content = {
        ChooseSimDialogSheetContent { selectedSim ->
            onSimSelected.invoke(selectedSim)
        }
    }, onDismiss = {
        onSimSelected.invoke(-1)
    })
}

/**
 * TODO Read Device SIM and show numbers accordingly If one sim exist then show only one otherwise
 * show both of them and implement send SMS after select and confirm.
 */
@Composable
fun ChooseSimDialogSheetContent(onSimSelected: (Int) -> Unit) {
    val context = LocalContext.current
    var selectedSim by rememberSaveable { mutableIntStateOf(-1) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.verify_mobile_number),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.confirm_mobile_number_message),
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(id = R.string.bank_account_mobile_verification_conditions),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SimCard(
                simNumber = 1,
                isSelected = selectedSim == 1,
                onSimSelected = { selectedSim = 1 }
            )

            Spacer(modifier = Modifier.width(24.dp))
            Text(text = stringResource(id = R.string.or))
            Spacer(modifier = Modifier.width(24.dp))
            SimCard(
                simNumber = 2,
                isSelected = selectedSim == 2,
                onSimSelected = { selectedSim = 2 }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.regular_charges_will_apply),
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall
        )
        MifosButton(
            modifier = Modifier
                .width(200.dp)
                .padding(top = 16.dp),
            onClick = {
                if (selectedSim == -1) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.choose_a_sim),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onSimSelected(selectedSim)
                }
            }
        ) {
            Text(text = stringResource(id = R.string.confirm))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SimCard(
    simNumber: Int, isSelected: Boolean, onSimSelected: () -> Unit
) {
    val drawable: Painter = painterResource(
        id = if (isSelected) {
            R.drawable.sim_card_selected
        } else R.drawable.sim_card_unselected
    )
    Image(painter = drawable,
        contentDescription = "SIM Card $simNumber",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(50.dp)
            .clickable { onSimSelected() }
    )
}

@Preview
@Composable
fun SimSelectionPreview() {
    MifosTheme {
        ChooseSimDialogSheetContent(onSimSelected = {})
    }
}
