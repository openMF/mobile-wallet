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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mobile_wallet.feature.auth.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.common.utils.isValidEmail
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.SearchRepository
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.model.client.ClientAddress
import org.mifospay.core.model.client.NewClient
import org.mifospay.core.model.user.NewUser
import org.mifospay.core.ui.PasswordStrengthState
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.core.ui.utils.PasswordChecker
import org.mifospay.core.ui.utils.PasswordStrength
import org.mifospay.core.ui.utils.PasswordStrengthResult
import org.mifospay.feature.auth.signup.SignUpAction.Internal.ReceivePasswordStrengthResult

private const val KEY_STATE = "signup_state"

class SignupViewModel(
    private val userRepository: UserRepository,
    private val searchRepository: SearchRepository,
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SignUpState, SignUpEvent, SignUpAction>(
    initialState = savedStateHandle[KEY_STATE] ?: SignUpState(),
) {
    private var passwordStrengthJob: Job = Job().apply { complete() }

    init {
//        stateFlow
//            .onEach { savedStateHandle[KEY_STATE] = it }
//            .launchIn(viewModelScope)

        savedStateHandle.get<String>("mobileNumber")?.let {
            viewModelScope.launch {
                trySendAction(SignUpAction.MobileNumberInputChange(it))
            }
        }

        savedStateHandle.get<Int>("savingsProductId")?.let {
            viewModelScope.launch {
                trySendAction(SignUpAction.SavingsAccountNoInputChange(it))
            }
        }

        savedStateHandle.get<String>("businessName")?.let {
            viewModelScope.launch {
                trySendAction(SignUpAction.BusinessNameInputChange(it))
            }
        }
    }

    override fun handleAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.FirstNameInputChange -> {
                mutableStateFlow.update {
                    it.copy(firstNameInput = action.firstName)
                }
            }

            is SignUpAction.LastNameInputChange -> {
                mutableStateFlow.update {
                    it.copy(lastNameInput = action.lastName)
                }
            }

            is SignUpAction.UserNameInputChange -> {
                mutableStateFlow.update {
                    it.copy(userNameInput = action.username)
                }
            }

            is SignUpAction.PasswordInputChange -> handlePasswordInput(action)

            is SignUpAction.ConfirmPasswordInputChange -> {
                mutableStateFlow.update {
                    it.copy(confirmPasswordInput = action.confirmPassword)
                }
            }

            is SignUpAction.EmailInputChange -> {
                mutableStateFlow.update {
                    it.copy(emailInput = action.email)
                }
            }

            is SignUpAction.AddressLine1InputChange -> {
                mutableStateFlow.update {
                    it.copy(addressLine1Input = action.addressLineOne)
                }
            }

            is SignUpAction.AddressLine2InputChange -> {
                mutableStateFlow.update {
                    it.copy(addressLine2Input = action.addressLineTwo)
                }
            }

            is SignUpAction.PinCodeInputChange -> {
                mutableStateFlow.update {
                    it.copy(pinCodeInput = action.pincode)
                }
            }

            is SignUpAction.BusinessNameInputChange -> {
                mutableStateFlow.update {
                    it.copy(businessNameInput = action.businessName)
                }
            }

            is SignUpAction.MobileNumberInputChange -> {
                mutableStateFlow.update {
                    it.copy(mobileNumberInput = action.mobileNumber)
                }
            }

            is SignUpAction.SavingsAccountNoInputChange -> {
                mutableStateFlow.update {
                    it.copy(savingsProductId = action.savingsAccountNo)
                }
            }

            is SignUpAction.StateInputChange -> {
                mutableStateFlow.update {
                    it.copy(stateInput = action.state)
                }
            }

            is SignUpAction.CountryInputChange -> {
                mutableStateFlow.update {
                    it.copy(
                        countryInput = action.country,
                        // reset state when country changes
                        stateInput = "",
                    )
                }
            }

            is SignUpAction.CloseClick -> {
                sendEvent(SignUpEvent.NavigateBack)
            }

            is SignUpAction.ErrorDialogDismiss -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is ReceivePasswordStrengthResult -> handlePasswordStrengthResult(action)

            is SignUpAction.Internal.ReceiveRegisterResult -> handleSignUpResult(action)

            is SignUpAction.SubmitClick -> handleSubmitClick()

            is SignUpAction.LoadCountries -> loadCountriesFromJson()
        }
    }

    private fun handlePasswordInput(action: SignUpAction.PasswordInputChange) {
        // Update input:
        mutableStateFlow.update {
            it.copy(
                passwordInput = action.password,
                passwordFeedback = PasswordChecker.getPasswordFeedback(action.password),
            )
        }
        // Update password strength:
        passwordStrengthJob.cancel()
        if (action.password.isEmpty()) {
            mutableStateFlow.update {
                it.copy(passwordStrengthState = PasswordStrengthState.NONE)
            }
        } else {
            passwordStrengthJob = viewModelScope.launch {
                val result = PasswordChecker.getPasswordStrengthResult(action.password)
                trySendAction(ReceivePasswordStrengthResult(result))
            }
        }
    }

    private fun handlePasswordStrengthResult(action: ReceivePasswordStrengthResult) {
        when (val result = action.result) {
            is PasswordStrengthResult.Success -> {
                val updatedState = when (result.passwordStrength) {
                    PasswordStrength.LEVEL_0 -> PasswordStrengthState.WEAK_1
                    PasswordStrength.LEVEL_1 -> PasswordStrengthState.WEAK_2
                    PasswordStrength.LEVEL_2 -> PasswordStrengthState.WEAK_3
                    PasswordStrength.LEVEL_3 -> PasswordStrengthState.GOOD
                    PasswordStrength.LEVEL_4 -> PasswordStrengthState.STRONG
                    PasswordStrength.LEVEL_5 -> PasswordStrengthState.VERY_STRONG
                }
                mutableStateFlow.update { oldState ->
                    oldState.copy(passwordStrengthState = updatedState)
                }
            }

            is PasswordStrengthResult.Error -> {}
        }
    }

    private fun handleSignUpResult(action: SignUpAction.Internal.ReceiveRegisterResult) {
        when (val result = action.registerResult) {
            is DataState.Success -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                sendEvent(SignUpEvent.NavigateToLogin(result.data))
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(dialogState = SignUpDialog.Error(result.exception.message.toString()))
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update { it.copy(dialogState = SignUpDialog.Loading) }
            }
        }
    }

    // TODO:: move error messages to strings.xml
    private fun handleSubmitClick() = when {
        state.savingsProductId == 0 -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please select a savings account."))
            }
        }

        state.firstNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your first name."))
            }
        }

        state.lastNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your last name."))
            }
        }

        state.userNameInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your username."))
            }
        }

        state.emailInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your email."))
            }
        }

        !state.emailInput.isValidEmail() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter a valid email."))
            }
        }

        state.mobileNumberInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your mobile number."))
            }
        }

        state.mobileNumberInput.length < 10 -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Mobile number must be 10 digits long."))
            }
        }

        state.passwordInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(
                    dialogState = SignUpDialog.Error(
                        message = "The password field cannot be empty.",
                    ),
                )
            }
        }

        state.passwordFeedback.isNotEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error(state.passwordFeedback.toString()))
            }
        }

        state.confirmPasswordInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(
                    dialogState = SignUpDialog.Error(
                        message = "The confirm password field cannot be empty.",
                    ),
                )
            }
        }

        state.passwordInput != state.confirmPasswordInput -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Passwords do not match."))
            }
        }

        state.addressLine1Input.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your address line 1."))
            }
        }

        state.addressLine2Input.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your address line 2."))
            }
        }

        state.pinCodeInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your pin code."))
            }
        }

        state.countryInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your country."))
            }
        }

        state.stateInput.isEmpty() -> {
            mutableStateFlow.update {
                it.copy(dialogState = SignUpDialog.Error("Please enter your state."))
            }
        }

        else -> initiateSignUp()
    }

    /*
        Enhancement: Move the following code in to a Use Case
     */
    private fun initiateSignUp() {
        mutableStateFlow.update {
            it.copy(dialogState = SignUpDialog.Loading)
        }

        val fieldsToCheck = mapOf(
            "Username" to state.userNameInput,
            "Mobile Number" to state.mobileNumberInput,
        )
        checkUniqueFields(fieldsToCheck)
    }

    private fun checkUniqueFields(fields: Map<String, String>) {
        viewModelScope.launch {
            val results = fields.map { (label, value) ->
                async {
                    val result = searchRepository.searchResources(value, Constants.CLIENTS, false)
                    label to result
                }
            }.awaitAll()

            val errorMessages = results.mapNotNull { (label, result) ->
                when (result) {
                    is DataState.Loading -> {}
                    is DataState.Success -> {
                        if (result.data.isNotEmpty()) "$label already exists." else null
                    }

                    is DataState.Error ->
                        "Unable to check if $label is unique. Please try again later."
                }
            }

            if (errorMessages.isNotEmpty()) {
                mutableStateFlow.update {
                    it.copy(dialogState = SignUpDialog.Error(errorMessages.joinToString("\n")))
                }
            } else {
                val newUser = NewUser(
                    state.userNameInput,
                    state.firstNameInput,
                    state.lastNameInput,
                    state.emailInput,
                    state.passwordInput,
                )
                createUser(newUser)
            }
        }
    }

    private fun createUser(newUser: NewUser) {
        viewModelScope.launch {
            when (val result = userRepository.createUser(newUser)) {
                is DataState.Error -> {
                    val message = result.exception.message.toString()
                    mutableStateFlow.update {
                        it.copy(dialogState = SignUpDialog.Error(message))
                    }
                }

                is DataState.Success -> {
                    createClient(result.data)
                }

                is DataState.Loading -> Unit
            }
        }
    }

    private fun createClient(userId: Int) {
        viewModelScope.launch {
            val newClient = NewClient(
                firstname = state.firstNameInput,
                lastname = state.lastNameInput,
                externalId = state.userNameInput.plus("_client"),
                mobileNo = state.mobileNumberInput,
                savingsProductId = state.savingsProductId,
                address = ClientAddress(
                    addressLine1 = state.addressLine1Input,
                    addressLine2 = state.addressLine2Input,
                    postalCode = state.pinCodeInput,
                    stateProvinceId = state.stateInput,
                    countryId = state.countryInput,
                ),
            )

            when (val result = clientRepository.createClient(newClient)) {
                is DataState.Error -> {
                    deleteUser(userId)
                    val message = result.exception.message.toString()
                    mutableStateFlow.update {
                        it.copy(dialogState = SignUpDialog.Error(message))
                    }
                }

                is DataState.Success -> {
                    assignClientToUser(result.data, userId)
                }

                is DataState.Loading -> Unit
            }
        }
    }

    private fun assignClientToUser(clientId: Int, userId: Int) {
        viewModelScope.launch {
            when (val result = userRepository.assignClientToUser(userId, clientId)) {
                is DataState.Error -> {
                    deleteUser(userId)
                    deleteClient(clientId)
                    val message = result.exception.message.toString()
                    mutableStateFlow.update {
                        it.copy(dialogState = SignUpDialog.Error(message))
                    }
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = null)
                    }
                    sendEvent(SignUpEvent.ShowToast("Registration successful."))
                    sendAction(
                        SignUpAction.Internal.ReceiveRegisterResult(
                            DataState.Success(state.userNameInput),
                        ),
                    )
                }

                is DataState.Loading -> Unit
            }
        }
    }

    private fun deleteUser(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
        }
    }

    private fun deleteClient(clientId: Int) {
        viewModelScope.launch {
            clientRepository.deleteClient(clientId)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun loadCountriesFromJson() {
        Logger.d("Tag-viewmodel loadCountriesFromJson called")
        viewModelScope.launch {
            try {
                val json = Json {
                    // This will skip unexpected fields
                    ignoreUnknownKeys = true
                }
                val bytes = Res.readBytes("files/countries.json")
                val jsonString = bytes.decodeToString()
                val parsed = json.decodeFromString<List<Country>>(jsonString)
                val mapped = parsed.associate {
                    it.name to it.states.map { s -> s.name }.ifEmpty { listOf("N/A") }
                }

                Logger.d("Tag-viewmodel mapped: ${mapped.size}")

                mutableStateFlow.update {
                    it.copy(countriesWithStates = mapped)
                }
            } catch (e: Exception) {
                Logger.d("Failed to load countries.json: ${e.message}")
            }
        }
    }
}

@Parcelize
data class SignUpState(
    val savingsProductId: Int = 0,
    val firstNameInput: String = "",
    val lastNameInput: String = "",
    val emailInput: String = "",
    val userNameInput: String = "",
    val addressLine1Input: String = "",
    val addressLine2Input: String = "",
    val pinCodeInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val mobileNumberInput: String = "",
    val stateInput: String = "",
    val countryInput: String = "",
    val businessNameInput: String = "",
    val dialogState: SignUpDialog? = null,
    val passwordStrengthState: PasswordStrengthState = PasswordStrengthState.NONE,
    val passwordFeedback: List<String> = emptyList(),
    val countriesWithStates: Map<String, List<String>> = emptyMap(),
) : Parcelable

sealed interface SignUpDialog : Parcelable {
    @Parcelize
    data object Loading : SignUpDialog

    @Parcelize
    data class Error(val message: String) : SignUpDialog
}

sealed interface SignUpEvent {
    data object NavigateBack : SignUpEvent
    data class ShowToast(val message: String) : SignUpEvent
    data class NavigateToLogin(val username: String) : SignUpEvent
}

sealed interface SignUpAction {
    data class FirstNameInputChange(val firstName: String) : SignUpAction
    data class LastNameInputChange(val lastName: String) : SignUpAction
    data class EmailInputChange(val email: String) : SignUpAction
    data class UserNameInputChange(val username: String) : SignUpAction
    data class AddressLine1InputChange(val addressLineOne: String) : SignUpAction
    data class AddressLine2InputChange(val addressLineTwo: String) : SignUpAction
    data class PinCodeInputChange(val pincode: String) : SignUpAction
    data class BusinessNameInputChange(val businessName: String) : SignUpAction
    data class PasswordInputChange(val password: String) : SignUpAction
    data class ConfirmPasswordInputChange(val confirmPassword: String) : SignUpAction
    data class MobileNumberInputChange(val mobileNumber: String) : SignUpAction
    data class SavingsAccountNoInputChange(val savingsAccountNo: Int) : SignUpAction
    data class StateInputChange(val state: String) : SignUpAction
    data class CountryInputChange(val country: String) : SignUpAction

    data object SubmitClick : SignUpAction
    data object CloseClick : SignUpAction
    data object ErrorDialogDismiss : SignUpAction
    data object LoadCountries : SignUpAction

    sealed class Internal : SignUpAction {
        data class ReceiveRegisterResult(
            val registerResult: DataState<String>,
        ) : Internal()

        data class ReceivePasswordStrengthResult(
            val result: PasswordStrengthResult,
        ) : Internal()
    }
}
