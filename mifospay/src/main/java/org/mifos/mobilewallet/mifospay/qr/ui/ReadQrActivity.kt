package org.mifos.mobilewallet.mifospay.qr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.qr.QrContract;
import org.mifos.mobilewallet.mifospay.qr.presenter.ReadQrPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
    @BindView(R.id.btn_open_gallery)
    ImageButton mOpenGallery;
    private static final int SELECT_PHOTO = 100;

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

    @OnClick(R.id.btn_open_gallery)
    public void openGallery() {
        Intent photoPic = new Intent(Intent.ACTION_PICK);
        photoPic.setType("image/*");
        startActivityForResult(photoPic, SELECT_PHOTO);
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

    public String scanQRImage(Bitmap bMap) {
        String contents = null;
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        LuminanceSource source =
                new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap =
                new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.QR_DATA, contents);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return contents;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap bMap = BitmapFactory.decodeStream(imageStream);
                    scanQRImage(bMap);
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}