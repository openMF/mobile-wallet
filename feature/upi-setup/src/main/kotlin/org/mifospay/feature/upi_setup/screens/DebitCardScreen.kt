package org.mifospay.feature.upi_setup.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobilewallet.mifospay.ui.VerifyStepHeader
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.feature.upi_setup.viewmodel.DebitCardUiState
import org.mifospay.feature.upi_setup.viewmodel.DebitCardViewModel

@Composable
fun DebitCardScreen(
    viewModel: DebitCardViewModel = hiltViewModel(),
    verificationStatus: Boolean = false,
    isContentVisible: Boolean = true,
    onDebitCardVerified: (String) -> Unit,
    onDebitCardVerificationFailed: (String) -> Unit
) {
    val debitCardUiState by viewModel.debitCardUiState.collectAsStateWithLifecycle()
    DebitCardScreenWithHeaderAndContent(
        debitCardUiState = debitCardUiState,
        verificationStatus = verificationStatus,
        isContentVisible = isContentVisible,
        onDebitCardVerified = onDebitCardVerified,
        onDebitCardVerificationFailed = onDebitCardVerificationFailed
    )
}

@Composable
fun DebitCardScreenWithHeaderAndContent(
    debitCardUiState: DebitCardUiState = DebitCardUiState.Initials,
    verificationStatus: Boolean = false,
    isContentVisible: Boolean = true,
    onDebitCardVerified: (String) -> Unit,
    onDebitCardVerificationFailed: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(
                top = 15.dp,
                bottom = 15.dp,
                start = 10.dp,
                end = 10.dp
            )
        ) {
            VerifyStepHeader("Debit Card Details ", verificationStatus)
            if (isContentVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                ) {
                    //handle debit card ui state
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DebitCardScreenContents()
                        when (debitCardUiState) {
                            is DebitCardUiState.Initials -> {

                            }
                            is DebitCardUiState.Verifying -> {
                                MifosLoadingWheel(
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .align(Alignment.Center),
                                    contentDesc = "Verifying Debit Card"
                                )
                            }
                            is DebitCardUiState.Verified -> {
                                onDebitCardVerified((debitCardUiState).otp)
                            }
                            is DebitCardUiState.VerificationFailed -> {
                                onDebitCardVerificationFailed((debitCardUiState).errorMessage)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DebitCardScreenInitialsPreview() {
    MifosTheme {
        DebitCardScreenWithHeaderAndContent(debitCardUiState = DebitCardUiState.Initials,
            onDebitCardVerified = {},
            onDebitCardVerificationFailed = {})
    }
}

@Preview
@Composable
fun DebitCardScreenLoadingPreview() {
    MifosTheme {
        DebitCardScreenWithHeaderAndContent(debitCardUiState = DebitCardUiState.Verifying,
            onDebitCardVerified = {},
            onDebitCardVerificationFailed = {})
    }
}

@Preview
@Composable
fun DebitCardScreenVerificationFailedPreview() {
    MifosTheme {
        DebitCardScreenWithHeaderAndContent(debitCardUiState = DebitCardUiState.VerificationFailed("Error Message"),
            onDebitCardVerified = {},
            onDebitCardVerificationFailed = {})
    }
}