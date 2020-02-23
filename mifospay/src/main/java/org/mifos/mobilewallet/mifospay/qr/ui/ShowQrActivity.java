package org.mifos.mobilewallet.mifospay.qr.ui;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_show_qr);

        ButterKnife.bind(ShowQrActivity.this);

        setToolbarTitle(Constants.QR_CODE);
        showBackButton();
        mPresenter.attachView(this);

        final String qrData = getIntent().getStringExtra(Constants.QR_DATA);
        mShowQrPresenter.generateQr(qrData);
        tvQrData.setText(qrData);

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
        editTextDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = edittext.getText().toString();
                if (amount.equals("")) {
                    showToast("Please enter the Amount");
                    return;
                } else if (Double.parseDouble(amount) <= 0) {
                    showToast(Constants.PLEASE_ENTER_VALID_AMOUNT);
                    return;
                }
                mAmount = amount;
                tvQrData.setText(qrData + ", " + mAmount);
                generateQR(qrData + ", " + mAmount);
            }
        });
        editTextDialog.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAmount = null;
                tvQrData.setText(qrData);
                generateQR(qrData);
                showToast("Reset Amount Successful");
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
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    void generateQR(String qrData) {
        mShowQrPresenter.generateQr(qrData);
    }
}
