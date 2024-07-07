package org.mifospay.feature.standing.instruction

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.standinginstruction.DeleteStandingInstruction
import org.mifospay.core.data.domain.usecase.standinginstruction.FetchStandingInstruction
import org.mifospay.core.data.domain.usecase.standinginstruction.UpdateStandingInstruction
import javax.inject.Inject

@HiltViewModel
class StandingInstructionDetailsViewModel @Inject constructor(
    val mUseCaseHandler: UseCaseHandler
): ViewModel() {

    @Inject
    lateinit var fetchStandingInstruction: FetchStandingInstruction

    @Inject
    lateinit var updateStandingInstruction: UpdateStandingInstruction

    @Inject
    lateinit var deleteStandingInstruction: DeleteStandingInstruction

    private var _siDetailsUiState = MutableStateFlow<SiDetailsUiState>(SiDetailsUiState.Loading)
    var siDetailsUiState: StateFlow<SiDetailsUiState> = _siDetailsUiState

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    fun fetchStandingInstructionDetails(standingInstructionId: Long) {
        _siDetailsUiState.value = SiDetailsUiState.Loading
        mUseCaseHandler.execute(fetchStandingInstruction,
            FetchStandingInstruction.RequestValues(standingInstructionId), object :
                UseCase.UseCaseCallback<FetchStandingInstruction.ResponseValue> {

                override fun onSuccess(response: FetchStandingInstruction.ResponseValue) {
                    _siDetailsUiState.value =
                        SiDetailsUiState.ShowSiDetails(response.standingInstruction)
                }

                override fun onError(message: String) {
                    _updateSuccess.value = false
                }
            })
    }

     fun deleteStandingInstruction(standingInstructionId: Long) {
         _siDetailsUiState.value = SiDetailsUiState.Loading
        mUseCaseHandler.execute(deleteStandingInstruction,
            DeleteStandingInstruction.RequestValues(standingInstructionId), object :
                UseCase.UseCaseCallback<DeleteStandingInstruction.ResponseValue> {

                override fun onSuccess(response: DeleteStandingInstruction.ResponseValue) {
                   _deleteSuccess.value = true
                }

                override fun onError(message: String) {
                    _updateSuccess.value = false
                }
            })
    }

     fun updateStandingInstruction(standingInstruction: StandingInstruction) {
         _siDetailsUiState.value = SiDetailsUiState.Loading
        mUseCaseHandler.execute(updateStandingInstruction,
            UpdateStandingInstruction.RequestValues(standingInstruction.id, standingInstruction),
            object : UseCase.UseCaseCallback<UpdateStandingInstruction.ResponseValue> {

                override fun onSuccess(response: UpdateStandingInstruction.ResponseValue) {
                    _siDetailsUiState.value = SiDetailsUiState.ShowSiDetails(standingInstruction)
                }

                override fun onError(message: String) {
                    _updateSuccess.value = false
                }
            })
    }
}

sealed interface SiDetailsUiState {
    data object Loading: SiDetailsUiState
    data class ShowSiDetails(val standingInstruction: StandingInstruction): SiDetailsUiState

}