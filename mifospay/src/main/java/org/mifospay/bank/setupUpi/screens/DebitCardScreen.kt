package org.mifos.mobilewallet.mifospay.seup_upi.fragment

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobilewallet.mifospay.seup_upi.presenter.DebitCardUiState
import org.mifos.mobilewallet.mifospay.seup_upi.presenter.DebitCardViewModel
import org.mifospay.bank.setupUpi.screens.DebitCardScreenContents
import org.mifos.mobilewallet.mifospay.ui.VerifyStepHeader
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.R

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
            .padding(dimensionResource(id = R.dimen.value_20dp)),
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.value_15dp),
                bottom = dimensionResource(id = R.dimen.value_15dp),
                start = dimensionResource(id = R.dimen.value_10dp),
                end = dimensionResource(id = R.dimen.value_10dp)
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