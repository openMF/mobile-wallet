package org.mifospay.feature.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.repository.local.LocalRepository
import javax.inject.Inject

/**
 * Created by naman on 30/8/17.
 */
@HiltViewModel
class TransferViewModel @Inject constructor(
    val mUsecaseHandler: UseCaseHandler,
    val localRepository: LocalRepository,
    val mFetchAccount: FetchAccount
) : ViewModel() {

    private val _vpa = MutableStateFlow("")
    val vpa: StateFlow<String> = _vpa

    private val _mobile = MutableStateFlow("")
    val mobile: StateFlow<String> = _mobile

    private var _transferUiState = MutableStateFlow<TransferUiState>(TransferUiState.Loading)
    var transferUiState: StateFlow<TransferUiState> = _transferUiState

    private var _updateSuccess = MutableStateFlow<Boolean>(false)
    var updateSuccess: StateFlow<Boolean> = _updateSuccess

    init {
        fetchVpa()
        fetchMobile()
    }

    fun fetchVpa() {
        viewModelScope.launch {
            _vpa.value = localRepository.clientDetails.externalId.toString()
        }
    }

    fun fetchMobile() {
        viewModelScope.launch {
            _mobile.value = localRepository.preferencesHelper.mobile.toString()
        }
    }

    fun checkSelfTransfer(externalId: String?): Boolean {
        return externalId == localRepository.clientDetails.externalId
    }

    fun checkBalanceAvailability(externalId: String, transferAmount: Double) {
        mUsecaseHandler.execute(mFetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    _transferUiState.value = TransferUiState.Loading
                    if (transferAmount > response.account.balance) {
                        _updateSuccess.value = true
                    } else {
                       _transferUiState.value = TransferUiState.ShowClientDetails(externalId, transferAmount)
                    }
                }

                override fun onError(message: String) {
                    _updateSuccess.value = false
                }
            })
    }
}


sealed interface TransferUiState {
    data object Loading: TransferUiState
    data class ShowClientDetails(val externalId: String, val transferAmount: Double): TransferUiState
}