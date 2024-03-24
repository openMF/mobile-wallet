package org.mifos.mobilewallet.mifospay.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifos.mobilewallet.mifospay.designsystem.theme.LocalTintTheme
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme

@Composable
fun EmptyContentScreen(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    imageContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
                .testTag("mifos:empty"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            imageContent()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun EmptyContentScreen(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    iconTint: Color = LocalTintTheme.current.iconTint,
    iconDrawable: Int
) {
    EmptyContentScreen(
        modifier = modifier,
        title = title,
        subTitle = subTitle,
        imageContent = {
            Image(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = iconDrawable),
                colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
                contentDescription = null,
            )
        }
    )
}

@Composable
fun EmptyContentScreen(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    iconTint: Color = LocalTintTheme.current.iconTint,
    iconImageVector: ImageVector = Icons.Rounded.Search
) {
    EmptyContentScreen(
        modifier = modifier,
        title = title,
        subTitle = subTitle,
        imageContent = {
            Icon(
                modifier = Modifier.size(64.dp),
                imageVector = iconImageVector,
                contentDescription = null,
                tint = iconTint
            )
        }
    )
}

@Preview(device = "id:pixel_5")
@Composable
fun EmptyContentScreenDrawableImagePreview() {
    MifosTheme {
        EmptyContentScreen(
            modifier = Modifier,
            title = "No data found",
            subTitle = "Please check you connection or try again",
            iconTint = MaterialTheme.colorScheme.primary,
            iconDrawable = R.drawable.baseline_info_outline_24
        )
    }
}

@Preview(device = "id:pixel_5")
@Composable
fun EmptyContentScreenImageVectorPreview() {
    MifosTheme {
        EmptyContentScreen(
            modifier = Modifier,
            title = "No data found",
            subTitle = "Please check you connection or try again",
            iconTint = MaterialTheme.colorScheme.primary,
            iconImageVector = Icons.Rounded.Search
        )
    }
}