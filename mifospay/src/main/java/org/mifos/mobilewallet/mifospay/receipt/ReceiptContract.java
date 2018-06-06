package org.mifos.mobilewallet.mifospay.receipt;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import okhttp3.ResponseBody;

/**
 * Created by ankur on 06/June/2018
 */

public interface ReceiptContract {

    interface ReceiptView extends BaseView<ReceiptPresenter> {

        void showSnackbar(String message);

        void writeReceipt(ResponseBody responseBody, String filename);

        void hideProgressDialog();
    }

    interface ReceiptPresenter extends BasePresenter {

        void fetchReceipt(String transactionId);
    }
}
