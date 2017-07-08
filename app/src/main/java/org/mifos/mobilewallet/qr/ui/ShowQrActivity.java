package org.mifos.mobilewallet.qr.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import org.mifos.mobilewallet.R;
import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.auth.presenter.AddAccountPresenter;
import org.mifos.mobilewallet.auth.ui.AddAccountActivity;
import org.mifos.mobilewallet.core.BaseActivity;
import org.mifos.mobilewallet.qr.QrContract;
import org.mifos.mobilewallet.qr.presenter.ShowQrPresenter;

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

    @BindView(R.id.tv_qr_data)
    TextView tvQrData;

    @BindView(R.id.tv_qr_amount)
    TextView tvQrAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_show_qr);

        ButterKnife.bind(ShowQrActivity.this);

        setToolbarTitle("QR code");
        showBackButton();
        mPresenter.attachView(this);

        mShowQrPresenter.generateQr("Test data", 100);
        tvQrData.setText("Test data");
        tvQrAmount.setText(String.valueOf(100));

    }

    @Override
    public void setPresenter(QrContract.ShowQrPresenter presenter) {
        this.mShowQrPresenter = presenter;
    }

    @Override
    public void showGeneratedQr(Bitmap bitmap) {
        ivQrCode.setImageBitmap(bitmap);

    }
}
