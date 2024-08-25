/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.mifos.library.countrycodepicker.CountryCodePicker
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.kyc.R
import java.time.format.DateTimeFormatter

@Composable
internal fun KYCLevel1Screen(
    navigateToKycLevel2: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KYCLevel1ViewModel = hiltViewModel(),
) {
    val kyc1uiState by viewModel.kyc1uiState.collectAsStateWithLifecycle()

    KYCLevel1Screen(
        uiState = kyc1uiState,
        submitData = viewModel::submitData,
        navigateToKycLevel2 = navigateToKycLevel2,
        modifier = modifier,
    )
}

@Composable
private fun KYCLevel1Screen(
    uiState: KYCLevel1UiState,
    submitData: (KYCLevel1DetailsState) -> Unit,
    navigateToKycLevel2: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Kyc1Form(
        submitData = submitData,
        modifier = modifier,
    )

    when (uiState) {
        KYCLevel1UiState.Loading -> {
            MfOverlayLoadingWheel(
                contentDesc = stringResource(id = R.string.feature_kyc_submitting),
            )
        }

        KYCLevel1UiState.Error -> {
            Toast.makeText(
                context,
                stringResource(R.string.feature_kyc_error_adding_KYC_Level_1_details),
                Toast.LENGTH_SHORT,
            ).show()
            navigateToKycLevel2.invoke()
        }

        KYCLevel1UiState.Success -> {
            Toast.makeText(
                context,
                stringResource(R.string.feature_kyc_successkyc1),
                Toast.LENGTH_SHORT,
            ).show()
            navigateToKycLevel2.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Kyc1Form(
    submitData: (KYCLevel1DetailsState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var address1 by rememberSaveable { mutableStateOf("") }
    var address2 by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    val dateState = rememberUseCaseState()
    val dateFormatter =
        DateTimeFormatter.ofPattern(stringResource(R.string.feature_kyc_date_format))

    val kycDetails = KYCLevel1DetailsState(
        firstName = firstName,
        lastName = lastName,
        addressLine1 = address1,
        addressLine2 = address2,
        mobileNo = mobileNumber,
        dob = dateOfBirth,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        MifosOutlinedTextField(
            label = R.string.feature_kyc_first_name,
            value = firstName,
            onValueChange = {
                firstName = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
        MifosOutlinedTextField(
            label = R.string.feature_kyc_last_name,
            value = lastName,
            onValueChange = {
                lastName = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
        MifosOutlinedTextField(
            label = R.string.feature_kyc_address_line_1,
            value = address1,
            onValueChange = {
                address1 = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )
        MifosOutlinedTextField(
            label = R.string.feature_kyc_address_line_2,
            value = address2,
            onValueChange = {
                address2 = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )

        Box(
            modifier = Modifier
                .padding(vertical = 7.dp),
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            CountryCodePicker(
                modifier = Modifier,
                shape = RoundedCornerShape(3.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                ),
                onValueChange = { (code, phone), isValid ->
                    if (isValid) {
                        mobileNumber = code + phone
                    }
                },
                label = {
                    Text(stringResource(id = R.string.feature_kyc_phone_number))
                },
                keyboardActions = KeyboardActions { keyboardController?.hide() },
            )
        }

        CalendarDialog(
            state = dateState,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = true,
                style = CalendarStyle.MONTH,
            ),
            selection = CalendarSelection.Date { date ->
                dateOfBirth = dateFormatter.format(date)
            },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 9.dp)
                .clickable { dateState.show() }
                .border(
                    width = 1.dp,
                    color = Color.Black,
                )
                .padding(12.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
        ) {
            Text(
                text = dateOfBirth.ifEmpty { stringResource(R.string.feature_kyc_select_dob) },
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Button(
            onClick = {
                submitData(kycDetails)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 7.dp),
        ) {
            Text(text = stringResource(R.string.feature_kyc_submit))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Kyc1FormPreview() {
    MifosTheme {
        KYCLevel1Screen(
            uiState = KYCLevel1UiState.Loading,
            submitData = { _ -> },
            navigateToKycLevel2 = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Kyc1PreviewWithError() {
    MifosTheme {
        KYCLevel1Screen(
            uiState = KYCLevel1UiState.Error,
            submitData = { _ -> },
            navigateToKycLevel2 = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Kyc1FormPreviewWithSuccess() {
    MifosTheme {
        KYCLevel1Screen(
            uiState = KYCLevel1UiState.Success,
            submitData = { _ -> },
            navigateToKycLevel2 = {},
        )
    }
}
