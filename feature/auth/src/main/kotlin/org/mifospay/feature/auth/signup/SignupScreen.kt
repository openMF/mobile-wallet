/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.signup

import android.widget.Toast
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
import com.mifospay.core.model.State
import com.mifospay.core.model.signup.PasswordStrength
import com.mifospay.core.model.signup.SignupData
import org.mifospay.core.data.util.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MfPasswordTextField
import org.mifospay.core.designsystem.theme.styleMedium16sp
import org.mifospay.feature.auth.R
import org.mifospay.feature.auth.utils.ValidateUtil.isValidEmail
import java.util.Locale

@Composable
internal fun SignupScreen(
    savingProductId: Int,
    mobileNumber: String,
    country: String,
    email: String,
    firstName: String,
    lastName: String,
    businessName: String,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val stateList by viewModel.states.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.initSignupData(
            savingProductId = savingProductId,
            mobileNumber = mobileNumber,
            countryName = country,
            email = email,
            firstName = firstName,
            lastName = lastName,
            businessName = businessName,
        )
    }
    LaunchedEffect(viewModel.isLoginSuccess) {
        if (viewModel.isLoginSuccess) {
            onLoginSuccess.invoke()
        }
    }

    SignupScreenContent(
        modifier = modifier,
        showProgressState = viewModel.showProgress,
        data = viewModel.signupData,
        stateList = stateList,
        onCompleteRegistration = {
            viewModel.registerUser(it) { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        },
    )
}

@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod")
private fun SignupScreenContent(
    data: SignupData,
    stateList: List<State>,
    onCompleteRegistration: (SignupData) -> Unit,
    modifier: Modifier = Modifier,
    showProgressState: Boolean = false,
) {
    val context = LocalContext.current

    var firstName by rememberSaveable { mutableStateOf(data.firstName ?: "") }
    var lastName by rememberSaveable { mutableStateOf(data.lastName ?: "") }
    var email by rememberSaveable { mutableStateOf(data.email ?: "") }
    var userName by rememberSaveable {
        mutableStateOf(
            data.email?.ifEmpty { "" }
                ?: data.email?.let { it.substring(0, it.indexOf('@')) } ?: "",
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
        val isAnyFieldEmpty =
            firstName.isEmpty() ||
                lastName.isEmpty() ||
                email.isEmpty() ||
                userName.isEmpty() ||
                addressLine1.isEmpty() ||
                addressLine2.isEmpty() ||
                pinCode.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty() ||
                selectedState == null

        val isNameOfBusinessEmpty =
            data.mifosSavingsProductId == MIFOS_MERCHANT_SAVINGS_PRODUCT_ID &&
                nameOfBusiness.isEmpty()

        if (!email.isValidEmail()) {
            Toast
                .makeText(
                    context,
                    context.getString(R.string.feature_auth_validate_email),
                    Toast.LENGTH_SHORT,
                ).show()
            return
        }

        if (isAnyFieldEmpty || isNameOfBusinessEmpty) {
            Toast
                .makeText(
                    context,
                    context.getString(R.string.feature_auth_all_fields_are_mandatory),
                    Toast.LENGTH_SHORT,
                ).show()
            return
        }
    }

    fun completeRegistration() {
        val signUpData =
            data.copy(
                firstName = firstName,
                lastName = lastName,
                email = email,
                userName = userName,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                pinCode = pinCode,
                businessName = nameOfBusiness,
                password = password,
                stateId = selectedState?.id,
            )
        onCompleteRegistration.invoke(signUpData)
    }

    Box(modifier) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .focusable(!showProgressState),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    text = stringResource(id = R.string.feature_auth_complete_your_registration),
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                )
                Text(
                    modifier =
                        Modifier.padding(
                            top = 4.dp,
                            bottom = 32.dp,
                            start = 24.dp,
                            end = 24.dp,
                        ),
                    text = stringResource(id = R.string.feature_auth_all_fields_are_mandatory),
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .focusable(!showProgressState),
            ) {
                UserInfoTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    label = stringResource(id = R.string.feature_auth_first_name),
                    value = firstName,
                ) {
                    firstName = it
                }
                UserInfoTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    label = stringResource(id = R.string.feature_auth_last_name),
                    value = lastName,
                ) {
                    lastName = it
                }
                UserInfoTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    label = stringResource(id = R.string.feature_auth_username),
                    value = userName,
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
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    label = stringResource(id = R.string.feature_auth_email),
                    value = email,
                ) {
                    email = it
                }
                if (data.mifosSavingsProductId == MIFOS_MERCHANT_SAVINGS_PRODUCT_ID) {
                    UserInfoTextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                        label = stringResource(id = R.string.feature_auth_name_of_business),
                        value = nameOfBusiness,
                    ) {
                        nameOfBusiness = it
                    }
                }
                UserInfoTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    label = stringResource(id = R.string.feature_auth_address_line_1),
                    value = addressLine1,
                ) {
                    addressLine1 = it
                }
                UserInfoTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    label = stringResource(id = R.string.feature_auth_address_line_2),
                    value = addressLine2,
                ) {
                    addressLine2 = it
                }
                UserInfoTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    label = stringResource(id = R.string.feature_auth_pin_code),
                    value = pinCode,
                ) {
                    pinCode = it
                }
                HorizontalDivider(thickness = 8.dp, color = Color.White)
                MifosStateDropDownOutlinedTextField(
                    value = selectedState?.name ?: "",
                    label = stringResource(id = R.string.feature_auth_state),
                    stateList = stateList,
                ) {
                    selectedState = it
                }
                HorizontalDivider(thickness = 24.dp, color = Color.White)
                Button(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = true,
                    onClick = {
                        validateAllFields()
                        completeRegistration()
                    },
                    contentPadding = PaddingValues(12.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_auth_complete),
                        style = styleMedium16sp.copy(color = MaterialTheme.colorScheme.onPrimary),
                    )
                }
            }
        }

        if (showProgressState) {
            MfOverlayLoadingWheel(
                contentDesc = stringResource(id = R.string.feature_auth_please_wait),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MifosStateDropDownOutlinedTextField(
    value: String,
    label: String,
    stateList: List<State>,
    modifier: Modifier = Modifier,
    onSelectedState: (State) -> Unit = {},
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(),
            value = value,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            stateList.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = {
                        expanded = false
                        onSelectedState(it)
                    },
                )
            }
        }
    }
}

@Composable
private fun UserInfoTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
) {
    MfOutlinedTextField(
        value = value,
        label = label,
        onValueChange = onValueChange,
        modifier = modifier,
        isError = value.isEmpty(),
        errorMessage = stringResource(id = R.string.feature_auth_mandatory),
    )
}

@Composable
private fun PasswordAndConfirmPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        MfPasswordTextField(
            password = password,
            label = stringResource(id = R.string.feature_auth_password),
            isError = password.isEmpty() || password.length < 6,
            isPasswordVisible = isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onPasswordChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            errorMessage =
                if (password.isEmpty()) {
                    stringResource(id = R.string.feature_auth_password_cannot_be_empty)
                } else if (password.length < 6) {
                    stringResource(id = R.string.feature_auth_password_must_be_least_6_characters)
                } else {
                    null
                },
        )
        MfPasswordTextField(
            password = confirmPassword,
            label = stringResource(id = R.string.feature_auth_confirm_password),
            isError = confirmPassword.isEmpty() || password != confirmPassword,
            isPasswordVisible = isConfirmPasswordVisible,
            onTogglePasswordVisibility = onConfirmTogglePasswordVisibility,
            onPasswordChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            errorMessage =
                if (confirmPassword.isEmpty()) {
                    stringResource(id = R.string.feature_auth_confirm_password_cannot_empty)
                } else if (password != confirmPassword) {
                    stringResource(id = R.string.feature_auth_passwords_do_not_match)
                } else {
                    null
                },
        )
        if (password.length >= 6) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "${stringResource(id = R.string.feature_auth_password_strength)}${
                    getPasswordStrength(password).replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale.ENGLISH,
                            )
                        } else {
                            it.toString()
                        }
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

    val numTypesPresent =
        intArrayOf(
            hasUpperCase.toInt(),
            hasLowerCase.toInt(),
            hasNumbers.toInt(),
            hasSymbols.toInt(),
        ).sum()
    return PasswordStrength.entries[numTypesPresent].name
}

private fun Boolean.toInt() = if (this) 1 else 0

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
private fun SignupScreenPreview() {
    SignupScreenContent(
        showProgressState = false,
        data = SignupData(),
        stateList = listOf(),
        onCompleteRegistration = { },
    )
}
