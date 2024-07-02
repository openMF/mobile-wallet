package org.mifospay.feature.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.kyc.FetchKYCLevel1Details
import org.mifospay.core.data.repository.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class KYCDescriptionViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val fetchKYCLevel1DetailsUseCase: FetchKYCLevel1Details
) : ViewModel(){
    private val _kycdescriptionState =
        MutableStateFlow<KYCDescriptionUiState>(KYCDescriptionUiState.Loading)

    val kycdescriptionState: StateFlow<KYCDescriptionUiState> = _kycdescriptionState

    init {
        fetchCurrentLevel()
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            delay(2000)
            fetchCurrentLevel()
            _isRefreshing.emit(false)
        }
    }

    private fun fetchCurrentLevel() {
        fetchKYCLevel1DetailsUseCase.walletRequestValues =
            FetchKYCLevel1Details.RequestValues(mLocalRepository.clientDetails.clientId.toInt())
        val requestValues = fetchKYCLevel1DetailsUseCase.walletRequestValues
        mUseCaseHandler.execute(fetchKYCLevel1DetailsUseCase, requestValues,
            object : UseCase.UseCaseCallback<FetchKYCLevel1Details.ResponseValue> {
                override fun onSuccess(response: FetchKYCLevel1Details.ResponseValue) {
                    if (response.kycLevel1DetailsList.size == 1) {
                            _kycdescriptionState.value = KYCDescriptionUiState.KYCDescription(
                                response.kycLevel1DetailsList.first()!!
                            )
                    } else {
                        _kycdescriptionState.value = KYCDescriptionUiState.Error
                    }
                }

                override fun onError(message: String) {
                    _kycdescriptionState.value = KYCDescriptionUiState.Error
                }
            })
    }

}

sealed interface KYCDescriptionUiState {
    data class KYCDescription(
        val kycLevel1Details: KYCLevel1Details?
    ) : KYCDescriptionUiState

    data object Error : KYCDescriptionUiState
    data object Loading : KYCDescriptionUiState
}