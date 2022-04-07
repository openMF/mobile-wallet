package org.mifos.mobilewallet.mifospay.bank.presenter

import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.BankAccountDetailView
import org.mifos.mobilewallet.mifospay.base.BaseView
import javax.inject.Inject

/**
 * Created by ankur on 09/July/2018
 */
class BankAccountDetailPresenter @Inject constructor() : BankContract.BankAccountDetailPresenter {
    var mBankAccountDetailView: BankAccountDetailView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mBankAccountDetailView = baseView as BankAccountDetailView?
        mBankAccountDetailView?.setPresenter(this)
    }
}