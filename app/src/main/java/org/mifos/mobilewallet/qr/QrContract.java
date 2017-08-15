package org.mifos.mobilewallet.qr;

import android.graphics.Bitmap;

import org.mifos.mobilewallet.base.BasePresenter;
import org.mifos.mobilewallet.base.BaseView;

/**
 * Created by naman on 8/7/17.
 */

public interface QrContract {

    interface ShowQrView extends BaseView<ShowQrPresenter> {

        void showGeneratedQr(Bitmap bitmap);
    }

    interface ShowQrPresenter extends BasePresenter {

        void generateQr(String data);
    }
}
