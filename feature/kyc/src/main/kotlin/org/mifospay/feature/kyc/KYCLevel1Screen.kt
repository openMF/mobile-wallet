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
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.togitech.ccp.component.TogiCountryCodePicker
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.kyc.R
import java.time.format.DateTimeFormatter

@Composable
fun KYCLevel1Screen(
    viewModel: KYCLevel1ViewModel = hiltViewModel(),
    navigateToKycLevel2: () -> Unit
) {
    val kyc1uiState by viewModel.kyc1uiState.collectAsStateWithLifecycle()

    KYCLevel1Screen(
        uiState = kyc1uiState,
        submitData = { fname,
                       lname,
                       address1,
                       address2,
                       phoneno,
                       dob ->
            viewModel.submitData(
                fname.trim { it <= ' ' },
                lname.trim { it <= ' ' },
                address1.trim { it <= ' ' },
                address2.trim { it <= ' ' },
                phoneno.trim { it <= ' ' },
                dob.trim { it <= ' ' }
            )
        },
        navigateToKycLevel2 = navigateToKycLevel2
    )
}

@Composable
fun KYCLevel1Screen(
    uiState: KYCLevel1UiState,
    submitData: (
        String,
        String,
        String,
        String,
        String,
        String
    ) -> Unit,
    navigateToKycLevel2: () -> Unit
) {
    val context = LocalContext.current

    Kyc1Form(
        modifier = Modifier,
        submitData = submitData
    )

    when (uiState) {
        KYCLevel1UiState.Loading -> {
            MfOverlayLoadingWheel(stringResource(id = R.string.feature_kyc_submitting))
        }

        KYCLevel1UiState.Error -> {
            Toast.makeText(
                context,
                stringResource(R.string.feature_kyc_error_adding_KYC_Level_1_details),
                Toast.LENGTH_SHORT
            ).show()
            navigateToKycLevel2.invoke()
        }

        KYCLevel1UiState.Success -> {
            Toast.makeText(
                context,
                stringResource(R.string.feature_kyc_successkyc1),
                Toast.LENGTH_SHORT
            ).show()
            navigateToKycLevel2.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Kyc1Form(
    modifier: Modifier,
    submitData: (
        String,
        String,
        String,
        String,
        String,
        String
    ) -> Unit
) {

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var address1 by rememberSaveable { mutableStateOf("") }
    var address2 by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    val dateState = rememberUseCaseState()
    val dateFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.feature_kyc_date_format))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        MifosOutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = firstName,
            onValueChange = {
                firstName = it
            },
            label = R.string.feature_kyc_first_name,
        )
        MifosOutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = lastName,
            onValueChange = {
                lastName = it
            },
            label = R.string.feature_kyc_last_name,
        )
        MifosOutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = address1,
            onValueChange = {
                address1 = it
            },
            label = R.string.feature_kyc_address_line_1,
        )
        MifosOutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            value = address2,
            onValueChange = {
                address2 = it
            },
            label = R.string.feature_kyc_address_line_2,
        )

        Box(
            modifier = Modifier
                .padding(vertical = 7.dp)
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current
            TogiCountryCodePicker(
                modifier = Modifier,
                shape = RoundedCornerShape(3.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                onValueChange = { (code, phone), isValid ->
                    if (isValid) {
                        mobileNumber = code + phone
                    }
                },
                label = {
                    Text(stringResource(id = R.string.feature_kyc_phone_number))
                },
                keyboardActions = KeyboardActions { keyboardController?.hide() }
            )
        }

        CalendarDialog(
            state = dateState,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = true,
                style = CalendarStyle.MONTH
            ),
            selection = CalendarSelection.Date { date ->
                dateOfBirth = dateFormatter.format(date)
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 9.dp)
                .clickable { dateState.show() }
                .border(
                    width = 1.dp,
                    color = Color.Black
                )
                .padding(12.dp)
                .clip(shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                text = dateOfBirth.ifEmpty { stringResource(R.string.feature_kyc_select_dob) },
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(
            onClick = {
                submitData(
                    firstName,
                    lastName,
                    address1,
                    address2,
                    mobileNumber,
                    dateOfBirth
                )
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
fun Kyc1FormPreview() {
    MifosTheme {
        KYCLevel1Screen(
            uiState = KYCLevel1UiState.Loading,
            submitData = { _, _, _, _, _, _ -> },
            navigateToKycLevel2 = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc1PreviewWithError() {
    MifosTheme {
        KYCLevel1Screen(
            uiState = KYCLevel1UiState.Error,
            submitData = { _, _, _, _, _, _ -> },
            navigateToKycLevel2 = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc1FormPreviewWithSuccess() {
    MifosTheme {
        KYCLevel1Screen(
            uiState = KYCLevel1UiState.Success,
            submitData = { _, _, _, _, _, _ -> },
            navigateToKycLevel2 = {}
        )
    }
}