package org.mifos.mobilewallet.mifospay.editprofile.ui

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.mifos.mobilewallet.mifospay.R

@Composable
fun EditProfileScreenImage(imageUri: Uri?, onCameraIconClick: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUri,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = { onCameraIconClick.invoke() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomEnd),
                colors = IconButtonDefaults.iconButtonColors(Color.Black)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}