package org.mifos.mobilewallet.mifospay.receipt;

import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import okhttp3.ResponseBody;

/**
 * This is a contract class working as an Interface for UI
 * and Presenter components for receipt package.
 * @author ankur
 * @since 6-June-2018
 */

public interface ReceiptContract {

    /**
     * Defines all the functions in UI component.
     */
    interface ReceiptView extends BaseView<ReceiptPresenter> {

        void showSnackbar(String message);

        void writeReceipt(ResponseBody responseBody, String filename);

        void hideProgressDialog();
    }

    /**
     * Defines all the functions in Presenter component.
     */
    interface ReceiptPresenter extends BasePresenter {

        void fetchReceipt(String transactionId);
    }
}
