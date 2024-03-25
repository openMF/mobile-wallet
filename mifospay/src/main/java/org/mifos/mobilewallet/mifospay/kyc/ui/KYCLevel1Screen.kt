package org.mifos.mobilewallet.mifospay.kyc.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel1UiState
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel1ViewModel
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import java.time.format.DateTimeFormatter


@Composable
fun KYCLevel1Screen(
    viewModel: KYCLevel1ViewModel = hiltViewModel(),
    navigateBack: () -> Unit
    ) {
    val context = LocalContext.current
    val kyc1uiState by viewModel.kyc1uiState.collectAsStateWithLifecycle()

    KYCLevel1Screen(
        context = context,
        uiState = kyc1uiState,
        submitData = { fname, lname,address1, address2,
                       phoneno, dob ->
            viewModel.submitData(
                fname.trim { it <= ' ' },
                lname.trim { it <= ' ' },
                address1.trim { it <= ' ' },
                address2.trim { it <= ' ' },
                phoneno.trim { it <= ' ' },
                dob.trim { it <= ' ' }
            )
        },
        navigateBack = navigateBack
    )
}


@Composable
fun KYCLevel1Screen(
    context:Context,
    uiState: KYCLevel1UiState,
    submitData: (String, String,String,String,String,String) -> Unit,
    navigateBack:() -> Unit
) {

    when(uiState){
        KYCLevel1UiState.EmptyForm -> {
            Kyc1Form(
                submitData = submitData
            )
        }

        KYCLevel1UiState.Error -> {
            Toast.makeText(context, stringResource(R.string.error_adding_KYC_Level_1_details_),Toast.LENGTH_SHORT).show()
            navigateBack.invoke()
        }

        KYCLevel1UiState.Success -> {
            Toast.makeText(context, stringResource(R.string.kyc_Level_1_details_added_successfully_),Toast.LENGTH_SHORT).show()
            navigateBack.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Kyc1Form(
    submitData: (String, String,String,String,String,String) -> Unit
){

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var address1 by remember { mutableStateOf("") }
    var address2 by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    val dateState = rememberUseCaseState()
    val dateFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.date_format))

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it },
            label = {
                Text(stringResource(R.string.first_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp)
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it },
            label = {
                Text(stringResource(R.string.last_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp)
        )
        OutlinedTextField(
            value = address1,
            onValueChange = {
                address1 = it },
            label = {
                Text(stringResource(R.string.address_line_1)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp)
        )
        OutlinedTextField(
            value = address2,
            onValueChange = {
                address2 = it },
            label = {
                Text(stringResource(R.string.address_line_2)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp)
        )

        Box (
            modifier = Modifier
                .padding(vertical = 7.dp)
        ){
           val keyboardController = LocalSoftwareKeyboardController.current
            TogiCountryCodePicker(
                modifier = Modifier,
                shape = RoundedCornerShape(3.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                onValueChange = { (code, phone), isValid ->
                    if (isValid){
                        mobileNumber = code + phone
                    }
                },
                label = {
                    Text(stringResource(id = R.string.phone_number)) },
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
            selection = CalendarSelection.Date{ date ->
                dateOfBirth = dateFormatter.format(date)
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp)
                .clickable { dateState.show() }
                .border(
                    width = 1.dp,
                    color = Color.Black
                )
                .padding(16.dp)
                .clip(shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                text = dateOfBirth.ifEmpty { stringResource(R.string.select_dob) },
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
                    )},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 7.dp),
        ) {
           Text(text = stringResource(R.string.submit))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Kyc1FormPreview(){
    MifosTheme {
        Kyc1Form(submitData = { _, _, _, _, _, _ ->}  )
    }
}
