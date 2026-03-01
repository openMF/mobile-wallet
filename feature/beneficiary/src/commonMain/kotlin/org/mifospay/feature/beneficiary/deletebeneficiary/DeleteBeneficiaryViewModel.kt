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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

        viewModelScope.launch {
            val result = repository.deleteBeneficiary(beneficiaryId)

            when (result) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            dialogState = null,
                            deleteSuccessful = true,
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update {
                        it.copy(
                            dialogState = DeleteBeneficiaryState.DialogState.Error(
                                result.exception.message ?: result.exception.toString(),
                            ),
                        )
                    }
                }

                DataState.Loading -> {
                    // Already showing deleting state
                }
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
