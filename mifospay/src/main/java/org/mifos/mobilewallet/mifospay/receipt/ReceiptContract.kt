package org.mifos.mobilewallet.mifospay.receipt;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.base.BasePresenter;
import org.mifos.mobilewallet.mifospay.base.BaseView;

import okhttp3.ResponseBody;

/**
 * Created by ankur on 06/June/2018
 */

public interface ReceiptContract {

    interface ReceiptView extends BaseView<ReceiptPresenter> {

        void showSnackbar(String message);

        void writeReceiptToPDF(ResponseBody responseBody, String filename);

        void hideProgressDialog();

        void showTransactionDetail (Transaction transaction);

        void showTransferDetail (TransferDetail transferDetail);

        void openPassCodeActivity();

    }

    interface ReceiptPresenter extends BasePresenter {

        void downloadReceipt(String transactionId);

        void fetchTransaction(long transactionId);
    }
}
