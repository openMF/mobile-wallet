package org.mifos.mobilewallet.mifospay.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageInputOutput {

    Context context;
    String imageName;

    public ImageInputOutput(Context context, String imageName) {
        this.imageName = imageName;
        this.context = context;
    }

    public void savebitmap(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            Log.e("InputOutput", e.toString());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                Log.e("InputOutput", e.toString());
            }
        }
    }


    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("InputOutput", e.toString());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e("InputOutput", e.toString());
            }
        }
        return null;
    }

    @NonNull
    private File createFile() {
        File directory;
        directory = new File(context.getFilesDir() + "/" + "mifos-wallet");
        if (!directory.exists()) {
            directory.mkdir();
        }
        return new File(directory, imageName + ".png");
    }
}
