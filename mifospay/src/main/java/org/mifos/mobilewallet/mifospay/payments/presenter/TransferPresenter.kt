package org.mifos.mobilewallet.mifospay.payments.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.common.Constants
import javax.inject.Inject

/**
 * Created by naman on 30/8/17.
 */
@HiltViewModel
class TransferPresenter @Inject constructor(
    val mUsecaseHandler: UseCaseHandler,
    val localRepository: LocalRepository,
    val mFetchAccount: FetchAccount
) : ViewModel(), BaseHomeContract.TransferPresenter {

<<<<<<< HEAD
=======
    @Inject
    lateinit var mFetchAccount: FetchAccount

    private val _vpa = MutableStateFlow("")
    val vpa: StateFlow<String> = _vpa

    private val _mobile = MutableStateFlow("")
    val mobile: StateFlow<String> = _mobile

>>>>>>> 44e5129 (refactor #1531: migrated payments screen to compose)
    var mTransferView: BaseHomeContract.TransferView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mTransferView = baseView as BaseHomeContract.TransferView?
        mTransferView?.setPresenter(this)
    }

    init {
        fetchVpa()
        fetchMobile()
    }

    override fun fetchVpa() {
        viewModelScope.launch {
            _vpa.value = localRepository.clientDetails.externalId
        }
    }

    override fun fetchMobile() {
        viewModelScope.launch {
            _mobile.value = localRepository.preferencesHelper.mobile.toString()
        }
    }

    override fun checkSelfTransfer(externalId: String?): Boolean {
        return externalId == localRepository.clientDetails.externalId
    }

    override fun checkBalanceAvailability(externalId: String?, transferAmount: Double) {
        mUsecaseHandler.execute(mFetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    mTransferView?.hideSwipeProgress()
                    if (transferAmount > response.account.balance) {
                        mTransferView?.showSnackbar(Constants.INSUFFICIENT_BALANCE)
                    } else {
                        mTransferView?.showClientDetails(externalId, transferAmount)
                    }
                }

                override fun onError(message: String) {
                    mTransferView?.hideSwipeProgress()
                    mTransferView?.showToast(Constants.ERROR_FETCHING_BALANCE)
                }
            })
    }
}
