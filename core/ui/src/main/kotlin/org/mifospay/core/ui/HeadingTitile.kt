package org.mifos.mobilewallet.mifospay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifospay.core.designsystem.theme.MifosTheme


@Composable
fun VerifyStepHeader(text: String, isVerified: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 17.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            textAlign = TextAlign.Start,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
        IconButton(
            onClick = { },
        ) {
            if (isVerified)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (isVerified) MaterialTheme.colorScheme.onSurface else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
        }
    }
}
@Preview
@Composable
fun VerifyStepHeaderVerifiedPreview() {
    MifosTheme {
        VerifyStepHeader(text = "Debit card Details ", isVerified = true)
    }
}

@Preview
@Composable
fun VerifyStepHeaderUnverifiedPreview() {
    VerifyStepHeader(text = "Enter OTP ", isVerified = false)
}