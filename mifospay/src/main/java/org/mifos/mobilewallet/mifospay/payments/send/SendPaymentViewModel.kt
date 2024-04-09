package org.mifos.mobilewallet.mifospay.payments.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class SendPaymentViewModel @Inject constructor(
    private val useCaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val fetchAccount: FetchAccount
) : ViewModel() {

    private val _vpa = MutableStateFlow("")
    val vpa: StateFlow<String> = _vpa

    private val _mobile = MutableStateFlow("")
    val mobile: StateFlow<String> = _mobile

    init {
        fetchVpa()
        fetchMobile()
    }

    private fun fetchVpa() {
        viewModelScope.launch {
            _vpa.value = localRepository.clientDetails.externalId.toString()
        }
    }

    private fun fetchMobile() {
        viewModelScope.launch {
            _mobile.value = localRepository.preferencesHelper.mobile.toString()
        }
    }

    fun checkSelfTransfer(externalId: String?): Boolean {
        return externalId == localRepository.clientDetails.externalId
    }

    fun checkBalanceAvailability(externalId: String?, transferAmount: Double) {
        useCaseHandler.execute(fetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCase.UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                   /* mTransferView?.hideSwipeProgress()
                    if (transferAmount > response.account.balance) {
                        mTransferView?.showSnackbar(Constants.INSUFFICIENT_BALANCE)
                    } else {
                        mTransferView?.showClientDetails(externalId, transferAmount)
                    }*/
                }

                override fun onError(message: String) {
                 /*   mTransferView?.hideSwipeProgress()
                    mTransferView?.showToast(Constants.ERROR_FETCHING_BALANCE)*/
                }
            })
    }
}