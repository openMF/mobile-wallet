package org.mifos.mobilewallet.mifospay.kyc.presenter


import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.entity.kyc.KYCLevel1Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.kyc.UploadKYCLevel1Details
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class KYCLevel1ViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val uploadKYCLevel1DetailsUseCase: UploadKYCLevel1Details
) : ViewModel()  {

    private val _kyc1uiState =
        MutableStateFlow<KYCLevel1UiState>(KYCLevel1UiState.EmptyForm)
    val kyc1uiState: StateFlow<KYCLevel1UiState> = _kyc1uiState

    fun submitData(
        fname: String, lname: String, address1: String, address2: String,
        phoneno: String, dob: String
    ) {
        val kycLevel1Details =
            KYCLevel1Details(
                fname, lname, address1,
                address2, phoneno, dob, "1"
            )

        uploadKYCLevel1DetailsUseCase.walletRequestValues = UploadKYCLevel1Details.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            kycLevel1Details
        )
        val requestValues = uploadKYCLevel1DetailsUseCase.walletRequestValues
        mUseCaseHandler.execute(uploadKYCLevel1DetailsUseCase, requestValues,
            object : UseCase.UseCaseCallback<UploadKYCLevel1Details.ResponseValue> {
                override fun onSuccess(response: UploadKYCLevel1Details.ResponseValue) {
                    _kyc1uiState.value = KYCLevel1UiState.Success
                }

                override fun onError(message: String) {
                    _kyc1uiState.value = KYCLevel1UiState.Error
                }
            }
        )
    }
}

sealed interface KYCLevel1UiState {
    data object EmptyForm : KYCLevel1UiState
    data object Success : KYCLevel1UiState
    data object Error : KYCLevel1UiState
}
