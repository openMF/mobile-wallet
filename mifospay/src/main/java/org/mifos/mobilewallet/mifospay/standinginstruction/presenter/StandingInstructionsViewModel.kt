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
class StandingInstructionsViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val getAllStandingInstructions: GetAllStandingInstructions
) : ViewModel() {

    private val _siState = MutableStateFlow<SIUiState>(SIUiState.Loading)
    val siState: StateFlow<SIUiState> = _siState

    init {
        getAllSI()
    }

     private fun getAllSI() {
        val client = localRepository.clientDetails
        mUseCaseHandler.execute(getAllStandingInstructions,
            GetAllStandingInstructions.RequestValues(client.clientId), object :
                UseCase.UseCaseCallback<GetAllStandingInstructions.ResponseValue> {

                override fun onSuccess(response: GetAllStandingInstructions.ResponseValue) {
                    if (response.standingInstructionsList.isEmpty()) {
                        _siState.value = SIUiState.Empty
                    } else {
                        _siState.value = SIUiState.SIList(response.standingInstructionsList)
                    }
                }

                override fun onError(message: String) {
                    _siState.value = SIUiState.Error
                }
            })
    }
}

sealed class SIUiState {
    data class SIList(
        val standingInstructions: List<StandingInstruction>
    ) : SIUiState()
    data object Empty : SIUiState()
    data object Error : SIUiState()
    data object Loading : SIUiState()
}
