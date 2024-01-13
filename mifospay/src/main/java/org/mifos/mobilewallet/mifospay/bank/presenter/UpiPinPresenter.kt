package org.mifos.mobilewallet.mifospay.bank.presenter

import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.UpiPinView
import org.mifos.mobilewallet.mifospay.base.BaseView
import javax.inject.Inject

/**
 * Created by ankur on 13/July/2018
 */
class UpiPinPresenter @Inject constructor() : BankContract.UpiPinPresenter {
    var mUpiPinView: UpiPinView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mUpiPinView = baseView as UpiPinView?
    }
}