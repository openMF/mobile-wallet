package org.mifospay.standinginstruction.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SIContent(
    fromClientName: String,
    toClientName: String,
    validTill: String,
    amount: String
) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            text = fromClientName,
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = toClientName,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = amount,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
        }

        Text(
            text = validTill,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SIContentPreview() {
    SIContent("From Client", "To Client", "Date", "Amount")
}