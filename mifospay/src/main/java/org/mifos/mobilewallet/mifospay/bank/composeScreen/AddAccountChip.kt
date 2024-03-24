package org.mifos.mobilewallet.mifospay.bank.composeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddAccountChip(modifier: Modifier = Modifier, onAddAccountClicked: () -> Unit) {
    Chip(
        modifier = modifier,
        onClick = { onAddAccountClicked() },
        colors = ChipDefaults.chipColors(
            backgroundColor = Color.Black,
            contentColor = colorResource(id = R.color.colorPrimary)
        ),
        border = BorderStroke(width = 1.dp, color = Color.Gray),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = colorResource(id = R.color.colorPrimary),
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = stringResource(id = R.string.add_account),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = colorResource(id = R.color.colorPrimary),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun AddAccountChipPreview() {
    MifosTheme {
        AddAccountChip(onAddAccountClicked = { })
    }
}
