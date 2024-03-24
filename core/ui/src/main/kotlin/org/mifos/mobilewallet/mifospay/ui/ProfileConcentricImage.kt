package org.mifos.mobilewallet.mifospay.ui

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileImage(bitmap: Bitmap?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = CircleShape
                )
        ) {
            MifosUserImage(
                bitmap = bitmap,
                modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp)
            )
        }
    }
}