package org.mifospay.payments.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.R
import org.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class SendPaymentViewModel @Inject constructor(
    private val useCaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val fetchAccount: FetchAccount
) : ViewModel() {

    private val _showProgress = MutableStateFlow(false)
    val showProgress: StateFlow<Boolean> = _showProgress

    private val _vpa = MutableStateFlow("")
    val vpa: StateFlow<String> = _vpa

    private val _mobile = MutableStateFlow("")
    val mobile: StateFlow<String> = _mobile

    init {
        fetchVpa()
        fetchMobile()
    }

    fun updateProgressState(isVisible: Boolean) {
        _showProgress.update { isVisible }
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

    fun checkSelfTransfer(
        selfVpa: String?,
        selfMobile: String?,
        externalIdOrMobile: String?,
        sendMethodType: SendMethodType,
    ): Boolean {
        return when (sendMethodType) {
            SendMethodType.VPA -> {
                selfVpa.takeIf { !it.isNullOrEmpty() }?.let { it == externalIdOrMobile } ?: false
            }

            SendMethodType.MOBILE -> {
                selfMobile.takeIf { !it.isNullOrEmpty() }?.let { it == externalIdOrMobile } ?: false
            }
        }
    }

    fun checkBalanceAvailabilityAndTransfer(
        externalId: String?,
        transferAmount: Double,
        onAnyError: (Int) -> Unit,
        proceedWithTransferFlow: () -> Unit
    ) {
        updateProgressState(true)
        useCaseHandler.execute(fetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCase.UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    updateProgressState(false)
                    if (transferAmount > response.account.balance) {
                        onAnyError(R.string.insufficient_balance)
                    } else {
                        proceedWithTransferFlow.invoke()
                    }
                }

                override fun onError(message: String) {
                    updateProgressState(false)
                    onAnyError.invoke(R.string.error_fetching_balance)
                }
            })
    }
}