package org.mifos.mobilewallet.mifospay.qr;

import android.graphics.Bitmap;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

/**
 * @author naman
 * @since 8/7/17
 */
public interface QrContract {

    interface ShowQrView extends BaseView<ShowQrPresenter> {

        void showGeneratedQr(Bitmap bitmap);
    }

    interface ShowQrPresenter extends BasePresenter {

        void generateQr(String data);
    }

    interface ReadQrView extends BaseView<ReadQrPresenter> {


    }

    interface ReadQrPresenter extends BasePresenter {


    }
}
