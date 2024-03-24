package org.mifos.mobilewallet.mifospay.kyc.presenter

import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.entity.kyc.KYCLevel1Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.kyc.FetchKYCLevel1Details
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
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