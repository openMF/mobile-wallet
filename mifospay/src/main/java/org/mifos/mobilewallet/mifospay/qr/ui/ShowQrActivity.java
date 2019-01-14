package org.mifos.mobilewallet.mifospay.qr.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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

    @BindView(R.id.tv_qr_data)
    TextView tvQrData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_show_qr);

        ButterKnife.bind(ShowQrActivity.this);

        setToolbarTitle(Constants.QR_CODE);
        showBackButton();
        mPresenter.attachView(this);

        String qrData = getIntent().getStringExtra(Constants.QR_DATA);

        mShowQrPresenter.generateQr(qrData);
        tvQrData.setText(qrData);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
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
