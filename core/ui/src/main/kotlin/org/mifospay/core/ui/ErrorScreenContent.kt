package org.mifospay.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
fun ErrorScreenContent(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.core_ui_error_occurred),
    subTitle: String = stringResource(id = R.string.core_ui_please_check_your_connection_or_try_again),
    onClickRetry: () -> Unit = { }
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

            MifosButton(
                modifier = Modifier
                    .width(150.dp)
                    .padding(top = 16.dp),
                onClick = onClickRetry
            ) {
                Text(text = stringResource(id = R.string.core_ui_retry))
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
fun ErrorContentScreenDrawableImagePreview() {
    MifosTheme {
        ErrorScreenContent(
            modifier = Modifier,
            title = "Error Occurred!",
            subTitle = "Please check your connection or try again",
            onClickRetry = { }
        )
    }
}
