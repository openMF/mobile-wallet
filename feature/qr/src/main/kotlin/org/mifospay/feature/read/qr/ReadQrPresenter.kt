package org.mifospay.feature.read.qr

import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.base.BaseView
import org.mifospay.qr.QrContract
import org.mifospay.qr.QrContract.ReadQrView
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