package org.mifos.mobilewallet.mifospay.qr.ui;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.zxing.Result;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.qr.QrContract;
import org.mifos.mobilewallet.mifospay.qr.presenter.ReadQrPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    ToggleButton toggleButton;
    Camera camera;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        setContentView(R.layout.activity_read_qr);

        ButterKnife.bind(ReadQrActivity.this);

        setToolbarTitle(Constants.SCAN_CODE);
        showBackButton();
        mPresenter.attachView(this);

        mScannerView.setAutoFocus(true);

        toggleButton = (ToggleButton) findViewById(R.id.onOffFlashlight);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {

                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();

                } else {

                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    camera.release();

                }
            }
        });
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
