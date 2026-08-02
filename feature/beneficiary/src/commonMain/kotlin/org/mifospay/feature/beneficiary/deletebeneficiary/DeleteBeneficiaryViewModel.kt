/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.beneficiary.deletebeneficiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.submitHandler
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository

/**
 * ViewModel for handling beneficiary deletion.
 *
 * This is a standalone ViewModel that manages the delete flow independently
 * from the beneficiary list, preventing unwanted list refreshes during the
 * delete confirmation flow.
 */
class DeleteBeneficiaryViewModel(
    private val repository: SelfServiceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DeleteBeneficiaryState())
    val state: StateFlow<DeleteBeneficiaryState> = _state.asStateFlow()

    // Template idiom (core-base/store): the delete write goes through a SubmitHandler
    // instead of a hand-folded DataState result. The handler owns the
    // Submitting/Submitted/Failed lifecycle; we observe it here to drive this VM's
    // existing Deleting overlay + success flag + error sheet, so the public state
    // shape and the consuming Screen are unchanged.
    private val submitDelete = viewModelScope.submitHandler<String>()

    init {
        submitDelete.state
            .onEach { submitState ->
                when (submitState) {
                    is SubmitState.Submitting -> {
                        _state.update {
                            it.copy(dialogState = DeleteBeneficiaryState.DialogState.Deleting)
                        }
                    }

                    is SubmitState.Submitted -> {
                        _state.update {
                            it.copy(
                                dialogState = null,
                                deleteSuccessful = true,
                            )
                        }
                        submitDelete.reset()
                    }

                    is SubmitState.Failed -> {
                        val message = submitState.error.message
                            ?: submitState.error.toString()
                        _state.update {
                            it.copy(
                                dialogState = DeleteBeneficiaryState.DialogState.Error(message),
                            )
                        }
                        submitDelete.reset()
                    }

                    SubmitState.Idle -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Shows the delete confirmation bottom sheet.
     */
    fun showDeleteConfirmation(beneficiaryId: Long, beneficiaryName: String) {
        _state.update {
            it.copy(
                dialogState = DeleteBeneficiaryState.DialogState.Confirmation(
                    beneficiaryId = beneficiaryId,
                    beneficiaryName = beneficiaryName,
                ),
            )
        }
    }

    /**
     * Confirms deletion and executes the delete operation.
     */
    fun confirmDelete(beneficiaryId: Long) {
        _state.update {
            it.copy(dialogState = DeleteBeneficiaryState.DialogState.Deleting)
        }

        // Submit through the handler — it drives Submitting/Submitted/Failed,
        // observed in `init`. The block unwraps the repository's transitional
        // DataState result: return the value on success, throw on error so the
        // handler reports Failed.
        submitDelete.submit {
            when (val result = repository.deleteBeneficiary(beneficiaryId)) {
                is DataState.Success -> result.data
                is DataState.Error -> throw result.exception
                DataState.Loading -> error("deleteBeneficiary must not emit Loading")
            }
        }
    }

    /**
     * Dismisses the current dialog/bottom sheet.
     */
    fun dismissDialog() {
        _state.update {
            it.copy(dialogState = null)
        }
    }

    /**
     * Resets the delete success flag after it has been consumed.
     */
    fun consumeDeleteSuccess() {
        _state.update {
            it.copy(deleteSuccessful = false)
        }
    }
}

data class DeleteBeneficiaryState(
    val dialogState: DialogState? = null,
    val deleteSuccessful: Boolean = false,
) {
    sealed interface DialogState {
        data class Confirmation(
            val beneficiaryId: Long,
            val beneficiaryName: String,
        ) : DialogState

        data object Deleting : DialogState

        data class Error(val message: String) : DialogState
    }
}
