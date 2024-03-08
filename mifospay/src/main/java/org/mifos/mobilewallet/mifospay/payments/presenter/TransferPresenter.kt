package org.mifos.mobilewallet.mifospay.payments.presenter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.account.FetchAccount
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.utils.Constants
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

    var mTransferView: BaseHomeContract.TransferView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mTransferView = baseView as BaseHomeContract.TransferView?
        mTransferView!!.setPresenter(this)
    }

    override fun fetchVpa() {
        mTransferView!!.showVpa(localRepository.clientDetails.externalId)
    }

    override fun fetchMobile() {
        mTransferView!!.showMobile(localRepository.preferencesHelper.mobile)
    }

    override fun checkSelfTransfer(externalId: String?): Boolean {
        return externalId == localRepository.clientDetails.externalId
    }

    override fun checkBalanceAvailability(externalId: String?, transferAmount: Double) {
        mUsecaseHandler.execute(mFetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    mTransferView!!.hideSwipeProgress()
                    if (transferAmount > response.account.balance) {
                        mTransferView!!.showSnackbar(Constants.INSUFFICIENT_BALANCE)
                    } else {
                        mTransferView!!.showClientDetails(externalId, transferAmount)
                    }
                }

                override fun onError(message: String) {
                    mTransferView!!.hideSwipeProgress()
                    mTransferView!!.showToast(Constants.ERROR_FETCHING_BALANCE)
                }
            })
    }
}
