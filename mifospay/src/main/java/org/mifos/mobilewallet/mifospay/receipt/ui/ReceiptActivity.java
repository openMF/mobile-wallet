package org.mifos.mobilewallet.mifospay.receipt.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;


import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail;
import org.mifos.mobilewallet.core.domain.model.Transaction;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract;
import org.mifos.mobilewallet.mifospay.receipt.presenter.ReceiptPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.FileUtils;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class ReceiptActivity extends BaseActivity implements ReceiptContract.ReceiptView {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 48;

    @Inject
    ReceiptPresenter mPresenter;

    ReceiptContract.ReceiptPresenter mReceiptPresenter;


    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_operation)
    TextView tvOperation;
    @BindView(R.id.tv_name)
    TextView tvPaidToName;
    @BindView(R.id.tv_transaction_ID)
    TextView tvTransactionID;
    @BindView(R.id.tv_transaction_date)
    TextView tvDate;
    @BindView(R.id.tv_transaction_to_name)
    TextView tvTransToName;
    @BindView(R.id.tv_transaction_to_number)
    TextView tvTransToNumber;
    @BindView(R.id.tv_transaction_from_name)
    TextView tvTransFromName;
    @BindView(R.id.tv_transaction_from_number)
    TextView tvTransFromNumber;
    @BindView(R.id.tv_transaction_reciept)
    TextView tvReceiptLink;

    private String transactionId;
    private boolean isDebit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_receipt);

        ButterKnife.bind(this);
        setToolbarTitle(Constants.RECEIPT);
        showBackButton();
        mPresenter.attachView(this);

        Uri data = getIntent().getData();
        if (data != null) {
            String scheme = data.getScheme(); // "https"
            String host = data.getHost(); // "receipt.mifospay.com"
            List<String> params;
            try {
                params = data.getPathSegments();
                transactionId = params.get(0); // "transactionId"
                tvReceiptLink.setText(data.toString());
                tvReceiptLink.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(
                                Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(Constants.UNIQUE_RECEIPT_LINK,
                                tvReceiptLink.getText().toString());
                        cm.setPrimaryClip(clipData);
                        showSnackbar(Constants.UNIQUE_RECEIPT_LINK_COPIED_TO_CLIPBOARD);
                        return true;
                    }
                });
            } catch (IndexOutOfBoundsException e) {
                showToast("Invalid link used to open the App.");
            }
            showProgressDialog(Constants.PLEASE_WAIT);
            mPresenter.fetchTransaction(Long.parseLong(transactionId));
        }
    }

    @Override
    public void showTransactionDetail(Transaction transaction) {
        tvAmount.setText(transaction.getCurrency().getCode() + " " + transaction.getAmount());
        tvDate.setText(transaction.getDate());
        tvReceiptLink.setText(Constants.RECEIPT_DOMAIN + transaction.getTransactionId());
        tvTransactionID.setText(String.valueOf(transaction.getTransactionId()));


        switch (transaction.getTransactionType()) {
            case DEBIT:
                isDebit = true;
                tvOperation.setText("Paid to");
                tvOperation.setTextColor(Color.RED);
                break;
            case CREDIT:
                isDebit = false;
                tvOperation.setText("Credited By");
                tvOperation.setTextColor(Color.parseColor("#009688"));
                break;
            case OTHER:
                isDebit = false;
                tvOperation.setText(Constants.OTHER);
                tvOperation.setTextColor(Color.YELLOW);
                break;
        }

    }

    @Override
    public void showTransferDetail(TransferDetail transferDetail) {
        if (isDebit) {
            tvPaidToName.setText(transferDetail.getToClient().getDisplayName());
        } else {
            tvPaidToName.setText(transferDetail.getFromClient().getDisplayName());
        }
        tvTransToName.setText(Constants.NAME + transferDetail.getToClient().getDisplayName());
        tvTransToNumber.setText(Constants.ACCOUNT_NUMBER + transferDetail
                .getToAccount().getAccountNo());
        tvTransFromName.setText(Constants.NAME + transferDetail.getFromClient().getDisplayName());
        tvTransFromNumber.setText(Constants.ACCOUNT_NUMBER + transferDetail
                .getFromAccount().getAccountNo());
        hideProgressDialog();
    }

    @OnClick(R.id.fab_download)
    void initiateDownload() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission already granted
            showSnackbar("Downloading Receipt");
            mPresenter.downloadReceipt(transactionId);
        }
    }

    @Override
    public void setPresenter(ReceiptContract.ReceiptPresenter presenter) {
        mReceiptPresenter = presenter;
    }

    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    public void showSnackbar(String message) {
        Toaster.show(findViewById(android.R.id.content), message);
    }

    @Override
    public void writeReceiptToPDF(ResponseBody responseBody, String filename) {

        File mifosDirectory = new File(Environment.getExternalStorageDirectory(),
                Constants.MIFOSPAY);
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs();
        }
        File documentFile = new File(mifosDirectory.getPath(), filename);
        if (!FileUtils.writeInputStreamDataToFile(responseBody.byteStream(), documentFile)) {
            showToast(Constants.ERROR_DOWNLOADING_RECEIPT);
        } else {
            showSnackbar(Constants.RECEIPT_DOWNLOADED_SUCCESSFULLY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showSnackbar("Downloading Receipt");
                    mReceiptPresenter.downloadReceipt(transactionId);

                } else {
                    showToast(Constants.NEED_EXTERNAL_STORAGE_PERMISSION_TO_DOWNLOAD_RECEIPT);
                }
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}
