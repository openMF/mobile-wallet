package org.mifos.mobilewallet.mifospay.history.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.theme.chipSelectedColor
import org.mifos.mobilewallet.mifospay.designsystem.theme.lightGrey

@Composable
fun HistoryScreen() {
    var selectedChip by remember { mutableStateOf(TransactionType.OTHER) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Chip(
                selected = selectedChip == TransactionType.OTHER,
                onClick = { selectedChip = TransactionType.OTHER },
                label = stringResource(R.string.all)
            )
            Chip(
                selected = selectedChip == TransactionType.CREDIT,
                onClick = { selectedChip = TransactionType.CREDIT },
                label = stringResource(R.string.credits)
            )
            Chip(
                selected = selectedChip == TransactionType.DEBIT,
                onClick = { selectedChip = TransactionType.DEBIT },
                label = stringResource(R.string.debits)
            )
        }
    }
}

@Composable
fun Chip(selected: Boolean, onClick: () -> Unit, label: String) {
    val context = LocalContext.current
    val backgroundColor = if (selected) chipSelectedColor else lightGrey
    Button(
        onClick = {
            onClick()
            Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor)
    ) {
        Text(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            text = label,
            color = Color.Black
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}