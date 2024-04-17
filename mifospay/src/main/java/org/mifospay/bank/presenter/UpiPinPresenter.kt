package org.mifospay.bank.presenter

import org.mifospay.bank.BankContract
import org.mifospay.bank.BankContract.UpiPinView
import org.mifospay.base.BaseView
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