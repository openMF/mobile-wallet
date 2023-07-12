package org.mifos.mobilewallet.mifospay.qr.presenter

import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.qr.QrContract
import org.mifos.mobilewallet.mifospay.qr.QrContract.ReadQrView
import javax.inject.Inject

/**
 * Created by naman on 7/9/17.
 */
class ReadQrPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    QrContract.ReadQrPresenter {
    private var mReadQrView: ReadQrView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mReadQrView = baseView as ReadQrView?
        mReadQrView!!.setPresenter(this)
    }
}