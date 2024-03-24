package org.mifos.mobilewallet.mifospay.bank.composeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

@Composable
fun EmptyStatePlaceHolder(
    title: String,
    subtitle: String,
    drawableResId: Int
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = "Content Description Placeholder",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicText(
            text = title,
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicText(
            text = subtitle,
            style = MaterialTheme.typography.body2
        )
    }
}

@Preview
@Composable
fun PlaceholderStateLayoutPreview() {
    MifosTheme {
        EmptyStatePlaceHolder(
            title = "No Bank Accounts Linked",
            subtitle = "You have not linked any bank accounts yet",
            drawableResId = R.drawable.ic_empty_state
        )
    }
}