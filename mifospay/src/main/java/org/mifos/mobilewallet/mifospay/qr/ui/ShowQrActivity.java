package org.mifos.mobilewallet.mifospay.qr.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.OnClick;
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

    @BindView(R.id.share_qr)
    ImageView shareQR;

    Bitmap bitmap;
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
        this.bitmap = bitmap;
        ivQrCode.setImageBitmap(bitmap);

    }

    @OnClick(R.id.share_qr)
    public void shareQRImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),
                bitmap, "qr", null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(intent, "Share QR Image"));
    }
}
