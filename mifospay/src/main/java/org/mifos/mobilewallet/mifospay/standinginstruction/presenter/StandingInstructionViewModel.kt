package org.mifos.mobilewallet.mifospay.standinginstruction.presenter

import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.entity.standinginstruction.StandingInstruction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.standinginstruction.GetAllStandingInstructions
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class StandingInstructionViewModel @Inject constructor(
    val mUseCaseHandler: UseCaseHandler,
    val localRepository: LocalRepository,
    private val getAllStandingInstructions: GetAllStandingInstructions
) : ViewModel() {

    private val _standingInstructionsUiState =
        MutableStateFlow<StandingInstructionsUiState>(StandingInstructionsUiState.Initial)
    val standingInstructionsUiState: StateFlow<StandingInstructionsUiState> =
        _standingInstructionsUiState

    fun getAllSI() {
        val client = localRepository.clientDetails
        _standingInstructionsUiState.value = StandingInstructionsUiState.Loading
        mUseCaseHandler.execute(getAllStandingInstructions,
            GetAllStandingInstructions.RequestValues(client.clientId), object :
                UseCase.UseCaseCallback<GetAllStandingInstructions.ResponseValue> {

                override fun onSuccess(response: GetAllStandingInstructions.ResponseValue) {
                    if (response.standingInstructionsList.isEmpty()) {
                        _standingInstructionsUiState.value = StandingInstructionsUiState.EmptyState
                    } else {
                        _standingInstructionsUiState.value =
                            StandingInstructionsUiState.StandingInstructionList(response.standingInstructionsList)
                    }
                }

                override fun onError(message: String) {
                    _standingInstructionsUiState.value = StandingInstructionsUiState.Error(message)
                }
            })
    }

    init {
        getAllSI()
    }


}

sealed class StandingInstructionsUiState {
    data object Initial : StandingInstructionsUiState()
    data object Loading : StandingInstructionsUiState()
    data object EmptyState : StandingInstructionsUiState()
    data class Error(val message: String) : StandingInstructionsUiState()
    data class StandingInstructionList(val standingInstructionList: List<StandingInstruction>) :
        StandingInstructionsUiState()
}