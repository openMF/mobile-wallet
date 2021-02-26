package org.mifos.mobilewallet.mifospay.qr.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.qr.QrContract;
import org.mifos.mobilewallet.mifospay.qr.presenter.ShowQrPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by naman on 8/7/17.
 */

public class ShowQrActivity extends BaseActivity implements QrContract.ShowQrView {

    @Inject
    ShowQrPresenter mPresenter;

    QrContract.ShowQrPresenter mShowQrPresenter;

    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    @BindView(R.id.tv_qr_vpa)
    TextView tvQrData;

    @BindView(R.id.btn_set_amount)
    Button btnSetAmount;

    private String mAmount = null;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_show_qr);

        ButterKnife.bind(ShowQrActivity.this);

        setToolbarTitle(Constants.QR_CODE);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        mPresenter.attachView(this);

        final String qrData = getIntent().getStringExtra(Constants.QR_DATA);
        mShowQrPresenter.generateQr(qrData);
        tvQrData.setText(getString(R.string.vpa) + ": " + qrData);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);

        btnSetAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetAmountDialog(qrData);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_qr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share_qr:
                if (mBitmap != null) {
                    Uri imageUri = saveImage(mBitmap);
                    shareQr(imageUri);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private Uri saveImage(Bitmap bitmap) {
        File imagesFolder = new File(getCacheDir(), "codes");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_code.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this,
                    "org.mifos.mobilewallet.mifospay.provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return uri;
    }

    private void shareQr(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/png");
            intent = Intent.createChooser(intent, "Share Qr code");
            startActivity(intent);
        }
    }

    void showSetAmountDialog (final String qrData) {
        final AlertDialog.Builder editTextDialog = new AlertDialog.Builder(this);
        editTextDialog.setCancelable(false);
        editTextDialog.setTitle("Enter Amount");
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextDialog.setView(edittext);
        if (mAmount != null) {
            edittext.setText(mAmount);
        }
        editTextDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = edittext.getText().toString();
                if (amount.equals("")) {
                    showToast(getString(R.string.enter_amount));
                    return;
                } else if (Double.parseDouble(amount) <= 0) {
                    showToast(Constants.PLEASE_ENTER_VALID_AMOUNT);
                    return;
                }
                mAmount = amount;
                tvQrData.setText(getString(R.string.vpa) + ": " + qrData +
                        "\n" + getString(R.string.amount) + ": " + mAmount);
                generateQR(qrData + ", " + mAmount);
            }
        });
        editTextDialog.setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAmount = null;
                tvQrData.setText(getString(R.string.vpa) + ": " + qrData);
                generateQR(qrData);
                showToast("Reset Amount Successful");
            }
        });
        editTextDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        editTextDialog.show();
    }

    @Override
    public void setPresenter(QrContract.ShowQrPresenter presenter) {
        this.mShowQrPresenter = presenter;
    }

    @Override
    public void showGeneratedQr(Bitmap bitmap) {
        ivQrCode.setImageBitmap(bitmap);
        this.mBitmap = bitmap;
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    void generateQR(String qrData) {
        mShowQrPresenter.generateQr(qrData);
    }
}
