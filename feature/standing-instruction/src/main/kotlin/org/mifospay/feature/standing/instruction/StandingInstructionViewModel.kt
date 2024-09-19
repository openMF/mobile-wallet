/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.standinginstruction.GetAllStandingInstructions
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.feature.standing.instruction.StandingInstructionsUiState.Loading

class StandingInstructionViewModel (
    private val mUseCaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val getAllStandingInstructions: GetAllStandingInstructions,
) : ViewModel() {

    private val mInstructionState = MutableStateFlow<StandingInstructionsUiState>(Loading)
    val standingInstructionsUiState: StateFlow<StandingInstructionsUiState> = mInstructionState

    init {
        getAllSI()
    }

    private fun getAllSI() {
        val client = localRepository.clientDetails
        mInstructionState.value = Loading
        mUseCaseHandler.execute(
            getAllStandingInstructions,
            GetAllStandingInstructions.RequestValues(client.clientId),
            object :
                UseCase.UseCaseCallback<GetAllStandingInstructions.ResponseValue> {

                override fun onSuccess(response: GetAllStandingInstructions.ResponseValue) {
                    if (response.standingInstructionsList.isEmpty()) {
                        mInstructionState.value = StandingInstructionsUiState.Empty
                    } else {
                        mInstructionState.value =
                            StandingInstructionsUiState.StandingInstructionList(response.standingInstructionsList)
                    }
                }

                override fun onError(message: String) {
                    mInstructionState.value = StandingInstructionsUiState.Error(message)
                }
            },
        )
    }
}

sealed class StandingInstructionsUiState {
    data object Loading : StandingInstructionsUiState()
    data object Empty : StandingInstructionsUiState()
    data class Error(val message: String) : StandingInstructionsUiState()
    data class StandingInstructionList(val standingInstructionList: List<StandingInstruction>) :
        StandingInstructionsUiState()
}
