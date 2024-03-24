package org.mifos.mobilewallet.mifospay.home.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobilewallet.mifospay.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileItemCard(
    modifier: Modifier = Modifier,
    icon: Int,
    text: Int,
    onClick: () -> Unit,
) {
    val combinedModifier = modifier
        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
        .padding(16.dp)
        .clickable { onClick.invoke() }
    FlowRow(modifier = combinedModifier) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.Black
        )
        Text(
            modifier = if (text == R.string.edit_profile || text == R.string.settings) Modifier
                .padding(
                    start = 18.dp
                )
                .align(Alignment.CenterVertically) else Modifier,
            text = stringResource(id = text),
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
        )
        if (text == R.string.edit_profile || text == R.string.settings) {
            Spacer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
