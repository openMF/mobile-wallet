package org.mifospay.feature.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.kyc.R

@Composable
fun KYCLevel3Screen(
    viewModel: KYCLevel3ViewModel = hiltViewModel()
){
    val kyc3uiState by viewModel.kyc3uiState.collectAsStateWithLifecycle()

    KYCLevel3Screen(
        uiState = kyc3uiState
    )
}

@Composable
fun KYCLevel3Screen(
    uiState: KYCLevel3UiState
){

    Kyc3Form(
        modifier = Modifier
    )

    when (uiState) {
        KYCLevel3UiState.Loading -> {
            MfOverlayLoadingWheel(stringResource(id = R.string.feature_kyc_submitting))
        }

        KYCLevel3UiState.Error -> {
            //Todo : Implement Error state
        }

        KYCLevel3UiState.Success -> {
            //Todo : Implement Success state
        }
    }
}

@Composable
fun Kyc3Form(
    modifier: Modifier
) {
    var panIdValue by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        MifosOutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = panIdValue,
            onValueChange = {
                panIdValue = it
            },
            label = R.string.feature_kyc_pan_id,
        )

        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.feature_kyc_submit))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KYCLevel3ScreenPreview() {
    MifosTheme {
        Kyc3Form(modifier = Modifier)
    }
}