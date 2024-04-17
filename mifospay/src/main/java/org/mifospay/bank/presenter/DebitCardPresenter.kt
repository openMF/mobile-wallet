package org.mifospay.bank.presenter

import org.mifospay.MifosPayApp
import org.mifospay.R
import org.mifospay.bank.BankContract
import org.mifospay.bank.BankContract.DebitCardView
import org.mifospay.base.BaseView
import javax.inject.Inject

/**
 * Created by ankur on 13/July/2018
 */
class DebitCardPresenter @Inject constructor() : BankContract.DebitCardPresenter {
    var mDebitCardView: DebitCardView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mDebitCardView = baseView as DebitCardView?
        mDebitCardView!!.setPresenter(this)
    }

    override fun verifyDebitCard(s: String?, s1: String?, s2: String?) {
        val otp = "0000"
        if (s!!.length < 12 || s.length > 19) {
            mDebitCardView!!.verifyDebitCardError(
                MifosPayApp.context
                    ?.getString(R.string.debit_card_error_message)
            )
        } else {
            mDebitCardView!!.verifyDebitCardSuccess(otp)
        }
    }
}