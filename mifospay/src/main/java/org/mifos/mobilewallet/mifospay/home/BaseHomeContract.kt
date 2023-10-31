package org.mifos.mobilewallet.mifospay.home

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.domain.model.Account
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.client.Client
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by naman on 17/6/17.
 */
interface BaseHomeContract {
    interface BaseHomeView : BaseView<BaseHomePresenter?>
    interface BaseHomePresenter : BasePresenter {
        fun fetchClientDetails()
    }

    interface HomeView : BaseView<HomePresenter?> {
        fun showSnackbar(message: String?)
        fun setAccountBalance(account: Account?)
        fun showTransactionsHistory(transactions: List<Transaction?>?)
        fun showTransactionsError()
        fun showTransactionsEmpty()
        fun showBottomSheetActionButton()
        fun hideBottomSheetActionButton()
        fun showToast(message: String?)
        fun hideSwipeProgress()
        fun hideTransactionLoading()
    }

    interface HomePresenter : BasePresenter {
        fun fetchAccountDetails()
        fun fetchVpa()
        fun showMoreHistory(existingItemsCount: Int)
    }

    interface TransferView : BaseView<TransferPresenter?> {
        fun showVpa(vpa: String?)
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
        fun showMobile(mobileNo: String?)
        fun hideSwipeProgress()
        fun showClientDetails(externalId: String?, amount: Double)
    }

    interface TransferPresenter : BasePresenter {
        fun fetchVpa()
        fun checkSelfTransfer(externalId: String?): Boolean
        fun fetchMobile()
        fun checkBalanceAvailability(externalId: String?, transferAmount: Double)
    }

    interface MerchantTransferView : BaseView<MerchantTransferPresenter?> {
        fun showToast(message: String?)
        fun hideSwipeProgress()
        fun showPaymentDetails(externalId: String?, amount: Double)
        fun showTransactionFetching()
        fun showTransactions(transactions: List<Transaction?>?)
        fun showSpecificView(drawable: Int, title: Int, subtitle: Int)
    }

    interface MerchantTransferPresenter : BasePresenter {
        fun checkBalanceAvailability(externalId: String?, transferAmount: Double)
        fun fetchMerchantTransfers(merchantAccountNo: String?)
    }

    interface ProfileView : BaseView<ProfilePresenter?> {
        fun showProfile(client: Client?)
        fun showEmail(email: String?)
        fun showVpa(vpa: String?)
        fun showMobile(mobile: String?)
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
        fun fetchImageSuccess(responseBody: ResponseBody?)
    }

    interface ProfilePresenter : BasePresenter {
        fun fetchProfile()
        fun fetchAccountDetails()
        fun fetchClientImage()
    }
}