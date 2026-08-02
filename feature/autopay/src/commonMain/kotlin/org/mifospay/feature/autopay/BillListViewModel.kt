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
import kotlinx.serialization.Serializable
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.submitHandler
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.datastore.BillRepository
import org.mifospay.core.model.autopay.Bill
import org.mifospay.core.ui.utils.BaseViewModel

class BillListViewModel(
    savedStateHandle: SavedStateHandle,
    private val billRepository: BillRepository,
) : BaseViewModel<BillListState, BillListEvent, BillListAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: BillListState(),
) {

    companion object {
        private const val KEY_STATE = "bill_list_state"
    }

    // Template idiom (core-base/store): the one-shot delete goes through a SubmitHandler
    // instead of a hand-folded DataState result. The handler owns the
    // Submitting/Submitted/Failed lifecycle; we observe it to drive this screen's
    // existing loading/success (list-refresh)/error UX, so the Screen is unchanged.
    // The bills list itself is a continuous READ (getAllBills Flow), left untouched.
    private val submitDelete = viewModelScope.submitHandler<Unit>()

    init {
        submitDelete.state
            .onEach { submitState ->
                when (submitState) {
                    is SubmitState.Submitting -> {
                        mutableStateFlow.update { it.copy(isLoading = true) }
                    }

                    is SubmitState.Submitted -> {
                        sendEvent(BillListEvent.BillDeleted)
                        loadBills() // Reload the list
                        submitDelete.reset()
                    }

                    is SubmitState.Failed -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to delete bill: ${submitState.error.message}",
                            )
                        }
                        submitDelete.reset()
                    }

                    SubmitState.Idle -> Unit
                }
            }
            .launchIn(viewModelScope)

        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)

        loadBills()
    }

    override fun handleAction(action: BillListAction) {
        when (action) {
            is BillListAction.RefreshBills -> {
                loadBills()
            }
            is BillListAction.AddNewBill -> {
                addNewBill()
            }
            is BillListAction.EditBill -> {
                editBill(action.billId)
            }
            is BillListAction.DeleteBill -> {
                deleteBill(action.billId)
            }
        }
    }

    private fun loadBills() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true) }

            try {
                billRepository.getAllBills().collect { bills ->
                    mutableStateFlow.update {
                        it.copy(
                            bills = bills,
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load bills: ${e.message}",
                    )
                }
            }
        }
    }

    private fun addNewBill() {
        sendEvent(BillListEvent.NavigateToAddBill)
    }

    private fun editBill(billId: String) {
        sendEvent(BillListEvent.NavigateToEditBill(billId))
    }

    private fun deleteBill(billId: String) {
        // Submit through the handler — it drives Submitting/Submitted/Failed, observed
        // in `init`. The block unwraps the repository's transitional DataState result:
        // return Unit on success, throw on error so the handler reports Failed.
        submitDelete.submit {
            when (val result = billRepository.deleteBill(billId)) {
                is DataState.Success -> result.data
                is DataState.Error -> throw result.exception
                DataState.Loading -> error("deleteBill must not emit Loading")
            }
        }
    }
}

@Serializable
data class BillListState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val bills: List<Bill> = emptyList(),
)

sealed interface BillListEvent {
    data object NavigateToAddBill : BillListEvent
    data class NavigateToEditBill(val billId: String) : BillListEvent
    data object BillDeleted : BillListEvent
}

sealed interface BillListAction {
    data object RefreshBills : BillListAction
    data object AddNewBill : BillListAction
    data class EditBill(val billId: String) : BillListAction
    data class DeleteBill(val billId: String) : BillListAction
}
