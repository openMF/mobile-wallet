package org.mifospay.qr

import android.graphics.Bitmap
import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

/**
 * Created by naman on 8/7/17.
 */
interface QrContract {
    interface ShowQrView : BaseView<ShowQrPresenter?> {
        fun showGeneratedQr(bitmap: Bitmap?)
    }

    interface ShowQrPresenter : BasePresenter {
        fun generateQr(data: String?)
    }

    interface ReadQrView : BaseView<ReadQrPresenter?>
    interface ReadQrPresenter : BasePresenter
}