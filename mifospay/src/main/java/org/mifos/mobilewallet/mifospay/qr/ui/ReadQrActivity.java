package org.mifos.mobilewallet.mifospay.qr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.widget.Button;
import android.widget.Toast;
import butterknife.OnClick;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;

import com.google.zxing.common.HybridBinarizer;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
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

    private static final int SELECT_PHOTO = 100;
    @Inject
    ReadQrPresenter mPresenter;

    QrContract.ReadQrPresenter mReadQrPresenter;

    @BindView(R.id.scannerView)
    ZXingScannerView mScannerView;

    @BindView(R.id.from_gallery)
    Button imageFromGallery;

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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT)
                        .show();
            }
            Bitmap bMap = BitmapFactory.decodeStream(imageStream);
            int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                    bMap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                    bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new MultiFormatReader();
            try {
                Hashtable<DecodeHintType, Object> decodeHints =
                        new Hashtable<>();
                decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

                Result result = reader.decode(bitmap, decodeHints);
                String barcode =  result.getText().toString();

                if (barcode != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.QR_DATA, barcode);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT)
                            .show();
                }
            } catch (NotFoundException e) {
                Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
            } catch (ChecksumException e) {
                Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
            } catch (FormatException e) {
                Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
            }



        }
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

    @OnClick(R.id.from_gallery)
    public void openGallery() {

        Intent photoPic = new Intent(Intent.ACTION_PICK);
        photoPic.setType("image/*");
        startActivityForResult(photoPic, SELECT_PHOTO);

    }
}
