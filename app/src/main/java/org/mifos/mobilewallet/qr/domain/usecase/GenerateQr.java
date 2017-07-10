package org.mifos.mobilewallet.qr.domain.usecase;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.mobilewallet.core.UseCase;

import javax.inject.Inject;

/**
 * Created by naman on 8/7/17.
 */

public class GenerateQr extends UseCase<GenerateQr.RequestValues, GenerateQr.ResponseValue> {

    private static int WHITE = 0xFFFFFFFF;
    private static int BLACK = 0xFF000000;
    private final static int WIDTH = 500;

    @Inject
    public GenerateQr() {
    }

    @Override
    protected void executeUseCase(GenerateQr.RequestValues requestValues) {

        try {
            JSONObject qrData = new JSONObject();
            qrData.put("data", requestValues.data);
            qrData.put("amount", requestValues.amount);

            try {
                Bitmap bitmap = encodeAsBitmap(qrData.toString());

                if (bitmap != null) {
                    getUseCaseCallback().onSuccess(new ResponseValue(bitmap));
                } else {
                    getUseCaseCallback().onError("Error occurred");
                }

            } catch (WriterException e) {
               getUseCaseCallback().onError("Failed to write data to qr");
            }

        } catch (JSONException e) {
            getUseCaseCallback().onError("Failed to create json data");
        }
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String data;
        private final int amount;

        public RequestValues(String data, int amount) {
          this.data = data;
          this.amount = amount;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {


        private final Bitmap bitmap;

        public ResponseValue(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }

}
