package org.mifospay.core.ui.utility

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddCardChip(
    modifier: Modifier,
    onAddBtn: () -> Unit,
    text: Int,
    btnText: Int
) {
    AssistChip(
        modifier = modifier,
        onClick = { onAddBtn.invoke() },
        label = {
            Text(
                stringResource(text),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(btnText),
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.Black
        ),
    )
}