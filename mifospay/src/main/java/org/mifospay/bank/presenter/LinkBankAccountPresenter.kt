package org.mifospay.bank.presenter

import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.bank.BankContract
import org.mifospay.bank.BankContract.LinkBankAccountView
import org.mifospay.base.BaseView
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