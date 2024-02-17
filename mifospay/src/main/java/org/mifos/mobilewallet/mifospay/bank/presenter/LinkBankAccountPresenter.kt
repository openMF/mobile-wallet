package org.mifos.mobilewallet.mifospay.bank.presenter

import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.LinkBankAccountView
import org.mifos.mobilewallet.mifospay.base.BaseView
import java.util.Random
import javax.inject.Inject

/**
 * Created by ankur on 09/July/2018
 */
class LinkBankAccountPresenter @Inject constructor() : BankContract.LinkBankAccountPresenter {
    var mLinkBankAccountView: LinkBankAccountView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mLinkBankAccountView = baseView as LinkBankAccountView?
        mLinkBankAccountView!!.setPresenter(this)
    }

    override fun fetchBankAccountDetails(bankName: String?) {
        // TODO:: UPI API implement
        mLinkBankAccountView!!.addBankAccount(
            BankAccountDetails(
                bankName, "Ankur Sharma", "New Delhi",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
    }

    companion object {
        private val mRandom = Random()
    }
}