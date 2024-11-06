/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.upi.setup.screens

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mobile_wallet.feature.upi_setup.generated.resources.Res
import mobile_wallet.feature.upi_setup.generated.resources.feature_upi_setup_back
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.Constants
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.bank.BankAccountDetails
import org.mifospay.feature.upi.setup.viewmodel.SetUpUpiViewModal

@Composable
internal fun SetupUpiPinScreenRoute(
    type: String,
    index: Int,
    bankAccountDetails: BankAccountDetails,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    setUpViewModel: SetUpUpiViewModal = koinViewModel(),
) {
    SetupUpiPinScreen(
        type = type,
        index = index,
        bankAccountDetails = bankAccountDetails,
        otpText = setUpViewModel.requestOtp(bankAccountDetails),
        setupUpiPin = {
            setUpViewModel.setupUpiPin(bankAccountDetails, it)
        },
        onBackPress = onBackPress,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
internal fun SetupUpiPinScreen(
    type: String,
    index: Int,
    bankAccountDetails: BankAccountDetails,
    otpText: String,
    setupUpiPin: (String) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (type) {
                            Constants.SETUP -> Constants.SETUP_UPI_PIN
                            Constants.CHANGE -> Constants.CHANGE_UPI_PIN
                            Constants.FORGOT -> Constants.FORGOT_UPI_PIN
                            else -> ""
                        },
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPress,
                    ) {
                        Icon(
                            MifosIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.feature_upi_setup_back),
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SetUpUpiScreenContent(
                    type = type,
                    otpText = otpText,
                    correctlySettingUpi = {
                        setupUpiPin(it)
                    },
                )
            }
        },
    )
}

@Preview
@Composable
fun PreviewSetupUpiPin() {
    SetupUpiPinScreen(
        type = Constants.SETUP_UPI_PIN,
        index = 0,
        otpText = "907889",
        bankAccountDetails = getBankAccountDetails(),
        onBackPress = {},
        setupUpiPin = {},
    )
}

@Preview
@Composable
fun PreviewChangeUpi() {
    SetupUpiPinScreen(
        type = Constants.CHANGE,
        index = 0,
        otpText = "907889",
        bankAccountDetails = getBankAccountDetails(),
        onBackPress = {},
        setupUpiPin = {},
    )
}

@Preview
@Composable
fun PreviewForgetUpi() {
    SetupUpiPinScreen(
        type = Constants.FORGOT,
        index = 0,
        otpText = "907889",
        bankAccountDetails = getBankAccountDetails(),
        onBackPress = {},
        setupUpiPin = {},
    )
}

fun getBankAccountDetails(): BankAccountDetails {
    return BankAccountDetails(
        accountNo = "SBI",
        bankName = "Ankur Sharma",
        accountHolderName = "New Delhi",
        branch = "XXXXXXXX9990XXX " + " ",
        ifsc = "Savings",
        type = "Debit",
        isUpiEnabled = false,
        upiPin = "0000",
    )
}
