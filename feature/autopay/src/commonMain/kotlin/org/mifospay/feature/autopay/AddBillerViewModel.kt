/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.datastore.BillerRepository
import org.mifospay.core.model.autopay.Biller
import org.mifospay.core.model.autopay.BillerCategory
import org.mifospay.core.model.autopay.BillerFormData
import org.mifospay.core.model.autopay.BillerValidationResult
import org.mifospay.core.ui.utils.BaseViewModel
import kotlin.random.Random

class AddBillerViewModel(
    savedStateHandle: SavedStateHandle,
    private val billerRepository: BillerRepository,
) : BaseViewModel<AddBillerState, AddBillerEvent, AddBillerAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: AddBillerState(),
) {

    companion object {
        private const val KEY_STATE = "add_biller_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: AddBillerAction) {
        when (action) {
            is AddBillerAction.UpdateBillerName -> {
                updateBillerName(action.name)
            }
            is AddBillerAction.UpdateAccountNumber -> {
                updateAccountNumber(action.accountNumber)
            }
            is AddBillerAction.UpdateContactNumber -> {
                updateContactNumber(action.contactNumber)
            }
            is AddBillerAction.UpdateEmail -> {
                updateEmail(action.email)
            }
            is AddBillerAction.UpdateCategory -> {
                updateCategory(action.category)
            }
            is AddBillerAction.UpdateAddress -> {
                updateAddress(action.address)
            }
            is AddBillerAction.SaveBiller -> {
                saveBiller()
            }
            is AddBillerAction.ValidateForm -> {
                validateForm()
            }
        }
    }

    private fun updateBillerName(name: String) {
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(name = name),
                validationResult = it.validationResult.copy(nameError = null),
            )
        }
    }

    private fun updateAccountNumber(accountNumber: String) {
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(accountNumber = accountNumber),
                validationResult = it.validationResult.copy(accountNumberError = null),
            )
        }
    }

    private fun updateContactNumber(contactNumber: String) {
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(contactNumber = contactNumber),
                validationResult = it.validationResult.copy(contactNumberError = null),
            )
        }
    }

    private fun updateEmail(email: String) {
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(email = email),
                validationResult = it.validationResult.copy(emailError = null),
            )
        }
    }

    private fun updateCategory(category: BillerCategory) {
        mutableStateFlow.update {
            it.copy(
                formData = it.formData.copy(category = category),
                validationResult = it.validationResult.copy(categoryError = null),
            )
        }
    }

    private fun updateAddress(address: String) {
        mutableStateFlow.update {
            it.copy(formData = it.formData.copy(address = address))
        }
    }

    private fun validateForm(): BillerValidationResult {
        val currentState = stateFlow.value
        val formData = currentState.formData

        var isValid = true
        val nameError = if (formData.name.isBlank()) "Biller name is required" else null
        val accountNumberError = if (formData.accountNumber.isBlank()) "Account number is required" else null
        val contactNumberError = if (formData.contactNumber.isBlank()) "Contact number is required" else null
        val categoryError = if (formData.category == null) "Please select a category" else null

        val emailError = if (formData.email.isNotBlank() && !isValidEmail(formData.email)) {
            "Please enter a valid email address"
        } else {
            null
        }

        if (nameError != null || accountNumberError != null || contactNumberError != null ||
            categoryError != null || emailError != null
        ) {
            isValid = false
        }

        val validationResult = BillerValidationResult(
            isValid = isValid,
            nameError = nameError,
            accountNumberError = accountNumberError,
            contactNumberError = contactNumberError,
            emailError = emailError,
            categoryError = categoryError,
        )

        mutableStateFlow.update { it.copy(validationResult = validationResult) }
        return validationResult
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

    private fun generateUniqueId(): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val random = Random.nextInt(100000, 999999)
        return "$timestamp-$random"
    }

    private fun saveBiller() {
        val validationResult = validateForm()

        if (!validationResult.isValid) {
            return
        }

        mutableStateFlow.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val currentState = stateFlow.value
                val formData = currentState.formData

                val biller = Biller(
                    id = generateUniqueId(),
                    name = formData.name.trim(),
                    accountNumber = formData.accountNumber.trim(),
                    contactNumber = formData.contactNumber.trim(),
                    email = formData.email.takeIf { it.isNotBlank() },
                    category = formData.category!!,
                    address = formData.address.takeIf { it.isNotBlank() },
                )

                val result = billerRepository.saveBiller(biller)

                when (result) {
                    is DataState.Loading -> {
                        // Loading state is already handled by setting isLoading = true above
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                            )
                        }
                        sendEvent(AddBillerEvent.BillerSaved(result.data))
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                            )
                        }
                    }
                }
            } catch (exception: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save biller: ${exception.message}",
                    )
                }
            }
        }
    }
}

@Serializable
data class AddBillerState(
    val formData: BillerFormData = BillerFormData(),
    val validationResult: BillerValidationResult = BillerValidationResult(isValid = false),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)

sealed interface AddBillerEvent {
    data class BillerSaved(val biller: Biller) : AddBillerEvent
}

sealed interface AddBillerAction {
    data class UpdateBillerName(val name: String) : AddBillerAction
    data class UpdateAccountNumber(val accountNumber: String) : AddBillerAction
    data class UpdateContactNumber(val contactNumber: String) : AddBillerAction
    data class UpdateEmail(val email: String) : AddBillerAction
    data class UpdateCategory(val category: BillerCategory) : AddBillerAction
    data class UpdateAddress(val address: String) : AddBillerAction
    data object SaveBiller : AddBillerAction
    data object ValidateForm : AddBillerAction
}
