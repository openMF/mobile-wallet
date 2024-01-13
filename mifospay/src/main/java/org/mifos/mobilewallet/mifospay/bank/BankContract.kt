package org.mifos.mobilewallet.mifospay.bank

import org.mifos.mobilewallet.core.domain.model.BankAccountDetails
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

/**
 * Created by ankur on 09/July/2018
 */
interface BankContract {
    interface BankAccountsPresenter : BasePresenter {
        fun fetchLinkedBankAccounts()
    }

    interface BankAccountsView : BaseView<BankAccountsPresenter?> {
        fun showLinkedBankAccounts(bankAccountList: List<BankAccountDetails?>?)
    }

    interface LinkBankAccountPresenter : BasePresenter {
        fun fetchBankAccountDetails(bankName: String?)
    }

    interface LinkBankAccountView : BaseView<LinkBankAccountPresenter?> {
        fun addBankAccount(bankAccountDetails: BankAccountDetails?)
    }

    interface BankAccountDetailPresenter : BasePresenter
    interface BankAccountDetailView : BaseView<BankAccountDetailPresenter?>
    interface DebitCardPresenter : BasePresenter {
        fun verifyDebitCard(s: String?, s1: String?, s2: String?)
    }

    interface DebitCardView : BaseView<DebitCardPresenter?> {
        fun verifyDebitCardSuccess(otp: String?)
        fun verifyDebitCardError(message: String?)
    }

    interface UpiPinPresenter : BasePresenter
    interface UpiPinView : BaseView<UpiPinPresenter?>
    interface SetupUpiPinPresenter : BasePresenter {
        fun setupUpiPin(bankAccountDetails: BankAccountDetails?, upiPin: String?)
        fun requestOtp(bankAccountDetails: BankAccountDetails?)
    }

    interface SetupUpiPinView : BaseView<SetupUpiPinPresenter?> {
        fun debitCardVerified(otp: String?)
        fun setupUpiPinSuccess(mSetupUpiPin: String?)
        fun setupUpiPinError(message: String?)
    }
}