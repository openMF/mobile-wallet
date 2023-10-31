package org.mifos.mobilewallet.mifospay.qr.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.qr.QrContract
import org.mifos.mobilewallet.mifospay.qr.QrContract.ShowQrView
import org.mifos.mobilewallet.mifospay.qr.domain.usecase.GenerateQr
import javax.inject.Inject

/**
 * Created by naman on 8/7/17.
 */
class ShowQrPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    QrContract.ShowQrPresenter {

    @Inject
    lateinit var generateQrUseCase: GenerateQr

    private lateinit var mShowQrView: ShowQrView
    override fun attachView(baseView: BaseView<*>?) {
        mShowQrView = baseView as ShowQrView
        mShowQrView.setPresenter(this)
    }

    override fun generateQr(data: String?) {
        mUsecaseHandler.execute(
            generateQrUseCase, data?.let { GenerateQr.RequestValues(it) },
            object : UseCaseCallback<GenerateQr.ResponseValue?> {
                override fun onSuccess(response: GenerateQr.ResponseValue?) {
                    if (response != null) {
                        mShowQrView.showGeneratedQr(response.bitmap)
                    }
                }

                override fun onError(message: String) {}
            })
    }
}