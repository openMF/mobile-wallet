package org.mifos.mobilewallet.mifospay.receipt.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

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
import okhttp3.ResponseBody;

public class ReceiptActivity extends BaseActivity implements ReceiptContract.ReceiptView {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 48;

    @Inject
    ReceiptPresenter mPresenter;

    ReceiptContract.ReceiptPresenter mReceiptPresenter;

    @BindView(R.id.pdfView_receipt)
    PDFView pdfViewReceipt;

    @BindView(R.id.tv_receiptLink)
    TextView tvReceiptLink;

    private String transactionId;

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
            List<String> params = data.getPathSegments();
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

            if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Permission already granted
                showProgressDialog(Constants.PLEASE_WAIT);
                mPresenter.fetchReceipt(transactionId);
            }
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
    public void writeReceipt(ResponseBody responseBody, String filename) {

        File mifosDirectory = new File(Environment.getExternalStorageDirectory(),
                Constants.MIFOSPAY);
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs();
        }

        File documentFile = new File(mifosDirectory.getPath(), filename);
        if (!FileUtils.writeInputStreamDataToFile(responseBody.byteStream(), documentFile)) {
            hideProgressDialog();
            showToast(Constants.ERROR_DOWNLOADING_RECEIPT);
        } else {
            pdfViewReceipt.fromFile(documentFile).load();
            hideProgressDialog();
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
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.

                    showProgressDialog(Constants.PLEASE_WAIT);
                    mReceiptPresenter.fetchReceipt(transactionId);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showToast(Constants.NEED_EXTERNAL_STORAGE_PERMISSION_TO_DOWNLOAD_RECEIPT);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
