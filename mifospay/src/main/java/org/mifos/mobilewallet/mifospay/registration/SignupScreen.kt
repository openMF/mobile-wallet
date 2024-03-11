package org.mifos.mobilewallet.mifospay.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.mobilewallet.model.State
import com.mifos.mobilewallet.model.signup.PasswordStrength
import com.mifos.mobilewallet.model.signup.SignupData
import org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MfOutlinedTextField
import org.mifos.mobilewallet.mifospay.designsystem.component.MfOverlayLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.component.MfPasswordTextField
import org.mifos.mobilewallet.mifospay.theme.styleMedium16sp
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.ValidateUtil.isValidEmail
import java.util.Locale


@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    val stateList by viewModel.states.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.isLoginSuccess) {
        if (viewModel.isLoginSuccess) {
            onLoginSuccess.invoke()
        }
    }

    SignupScreen(
        showProgressState = viewModel.showProgress,
        data = viewModel.signupData,
        stateList = stateList,
        onCompleteRegistration = {
            viewModel.registerUser(it) { message ->
                Toaster.showToast(context, message)
            }
        }
    )
}

@Composable
fun SignupScreen(
    showProgressState: Boolean = false,
    data: SignupData,
    stateList: List<State>,
    onCompleteRegistration: (SignupData) -> Unit
) {

    val context = LocalContext.current

    var firstName by rememberSaveable { mutableStateOf(data.firstName ?: "") }
    var lastName by rememberSaveable { mutableStateOf(data.lastName ?: "") }
    var email by rememberSaveable { mutableStateOf(data.email ?: "") }
    var userName by rememberSaveable {
        mutableStateOf(data.email?.ifEmpty { "" }
            ?: data.email?.let { it.substring(0, it.indexOf('@')) } ?: ""
        )
    }
    var addressLine1 by rememberSaveable { mutableStateOf("") }
    var addressLine2 by rememberSaveable { mutableStateOf("") }
    var pinCode by rememberSaveable { mutableStateOf("") }
    var nameOfBusiness by rememberSaveable { mutableStateOf(data.businessName ?: "") }

    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var selectedState by rememberSaveable { mutableStateOf<State?>(null) }

    fun validateAllFields() {
        val isAnyFieldEmpty = firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || userName.isEmpty() || addressLine1.isEmpty() || addressLine2.isEmpty()
                || pinCode.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || selectedState == null
        val isNameOfBusinessEmpty = data.mifosSavingsProductId == MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
                && nameOfBusiness.isEmpty()

        if (!email.isValidEmail()) {
            Toaster.showToast(context, context.getString(R.string.validate_email))
            return
        }

        if (isAnyFieldEmpty || isNameOfBusinessEmpty) {
            Toaster.showToast(context, context.getString(R.string.all_fields_are_mandatory))
            return
        }
    }

    fun completeRegistration() {
        val signUpData = data.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            userName = userName,
            addressLine1 = addressLine1,
            addressLine2 = addressLine2,
            pinCode = pinCode,
            businessName = nameOfBusiness,
            password = password,
            stateId = selectedState?.id
        )
        onCompleteRegistration.invoke(signUpData)
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .verticalScroll(rememberScrollState())
                .focusable(!showProgressState),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.onBackground),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    text = stringResource(id = R.string.complete_your_registration),
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                )
                Text(
                    modifier = Modifier.padding(
                        top = 4.dp, bottom = 32.dp, start = 24.dp, end = 24.dp
                    ),
                    text = stringResource(id = R.string.all_fields_are_mandatory),
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .focusable(!showProgressState)
            ) {
                UserInfoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    label = stringResource(id = R.string.first_name),
                    value = firstName
                ) {
                    firstName = it
                }
                UserInfoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.last_name),
                    value = lastName
                ) {
                    lastName = it
                }
                UserInfoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.username),
                    value = userName
                ) {
                    userName = it
                }
                PasswordAndConfirmPassword(
                    password = password,
                    onPasswordChange = { password = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    isPasswordVisible = isPasswordVisible,
                    onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                    isConfirmPasswordVisible = isConfirmPasswordVisible,
                    onConfirmTogglePasswordVisibility = {
                        isConfirmPasswordVisible = !isConfirmPasswordVisible
                    },
                )
                UserInfoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.email),
                    value = email
                ) {
                    email = it
                }
                if (data.mifosSavingsProductId == MIFOS_MERCHANT_SAVINGS_PRODUCT_ID) {
                    UserInfoTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = stringResource(id = R.string.name_of_business),
                        value = nameOfBusiness
                    ) {
                        nameOfBusiness = it
                    }
                }
                UserInfoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.address_line_1),
                    value = addressLine1
                ) {
                    addressLine1 = it
                }
                UserInfoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = stringResource(id = R.string.address_line_2),
                    value = addressLine2
                ) {
                    addressLine2 = it
                }
                UserInfoTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    label = stringResource(id = R.string.pin_code),
                    value = pinCode
                ) {
                    pinCode = it
                }
                HorizontalDivider(thickness = 8.dp, color = Color.White)
                MifosStateDropDownOutlinedTextField(
                    value = selectedState?.name ?: "",
                    label = stringResource(id = R.string.state),
                    stateList = stateList
                ) {
                    selectedState = it
                }
                HorizontalDivider(thickness = 24.dp, color = Color.White)
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = true,
                    onClick = {
                        validateAllFields()
                        completeRegistration()
                    },
                    contentPadding = PaddingValues(12.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.complete),
                        style = styleMedium16sp.copy(color = Color.White)
                    )
                }
            }
        }

        if (showProgressState) {
            MfOverlayLoadingWheel(stringResource(id = R.string.please_wait))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosStateDropDownOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    stateList: List<State>,
    onSelectedState: (State) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(),
            value = value,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            })
        {
            stateList.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = {
                        expanded = false
                        onSelectedState(it)
                    }
                )
            }
        }
    }
}

@Composable
fun UserInfoTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    MfOutlinedTextField(
        modifier = modifier,
        value = value,
        label = label,
        isError = value.isEmpty(),
        errorMessage = stringResource(id = R.string.mandatory),
        onValueChange = onValueChange
    )
}

@Composable
fun PasswordAndConfirmPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmTogglePasswordVisibility: () -> Unit,
) {
    Column {
        MfPasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            password = password,
            label = stringResource(id = R.string.password),
            isError = password.isEmpty() || password.length < 6,
            errorMessage = if (password.isEmpty()) {
                stringResource(id = R.string.password_cannot_be_empty)
            } else if (password.length < 6) {
                stringResource(id = R.string.password_must_be_least_6_characters)
            } else null,
            onPasswordChange = onPasswordChange,
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility
        )
        MfPasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            password = confirmPassword,
            label = stringResource(id = R.string.confirm_password),
            isError = confirmPassword.isEmpty() || password != confirmPassword,
            errorMessage = if (confirmPassword.isEmpty()) {
                stringResource(id = R.string.confirm_password_cannot_empty)
            } else if (password != confirmPassword) {
                stringResource(id = R.string.passwords_do_not_match)
            } else null,
            onPasswordChange = onConfirmPasswordChange,
            isPasswordVisible = isConfirmPasswordVisible,
            onTogglePasswordVisibility = onConfirmTogglePasswordVisibility
        )
        if (password.length >= 6) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "${stringResource(id = R.string.password_strength)}${
                    getPasswordStrength(password).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ENGLISH
                        ) else it.toString()
                    }
                }",
                color = getPasswordStrengthColor(password),
            )
        }
    }
}


private fun getPasswordStrength(password: String): String {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasNumbers = password.any { it.isDigit() }
    val hasSymbols = password.any { !it.isLetterOrDigit() }

    val numTypesPresent = intArrayOf(
        hasUpperCase.toInt(),
        hasLowerCase.toInt(),
        hasNumbers.toInt(),
        hasSymbols.toInt()
    ).sum()
    return PasswordStrength.entries[numTypesPresent].name
}

fun Boolean.toInt() = if (this) 1 else 0

private fun getPasswordStrengthColor(password: String): Color {
    val strength = getPasswordStrength(password)
    return when (PasswordStrength.valueOf(strength)) {
        PasswordStrength.WEAK -> Color.Red
        PasswordStrength.MODERATE -> Color.DarkGray
        PasswordStrength.STRONG -> Color.Green
        PasswordStrength.VERY_STRONG -> Color.Blue
        PasswordStrength.EXCELLENT -> Color.Magenta
        else -> Color.Black
    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        showProgressState = false,
        data = SignupData(),
        stateList = listOf(),
        onCompleteRegistration = { }
    )
}
