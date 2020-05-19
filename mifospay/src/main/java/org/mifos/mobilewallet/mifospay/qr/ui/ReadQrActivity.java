package org.mifos.mobilewallet.mifospay.qr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.google.zxing.Result;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.qr.QrContract;
import org.mifos.mobilewallet.mifospay.qr.presenter.ReadQrPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by naman on 7/9/17.
 */

public class ReadQrActivity extends BaseActivity implements QrContract.ReadQrView,
        ZXingScannerView.ResultHandler {

    @Inject
    ReadQrPresenter mPresenter;

    QrContract.ReadQrPresenter mReadQrPresenter;

    @BindView(R.id.scannerView)
    ZXingScannerView mScannerView;
    @BindView(R.id.btn_flash_on)
    ImageButton mFlashOn;
    @BindView(R.id.btn_flash_off)
    ImageButton mFlashOff;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_read_qr);

        ButterKnife.bind(ReadQrActivity.this);

        setToolbarTitle(Constants.SCAN_CODE);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        mPresenter.attachView(this);

        mScannerView.setAutoFocus(true);
    }

    @OnClick(R.id.btn_flash_on)
    void turnOnFlash() {
        mScannerView.setFlash(true);
        mFlashOn.setVisibility(View.GONE);
        mFlashOff.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_flash_off)
    void turnOffFlash() {
        mScannerView.setFlash(false);
        mFlashOn.setVisibility(View.VISIBLE);
        mFlashOff.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String qrData = result.getText();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.QR_DATA, qrData);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }

    @Override
    public void setPresenter(QrContract.ReadQrPresenter presenter) {
        this.mReadQrPresenter = presenter;
    }
}
