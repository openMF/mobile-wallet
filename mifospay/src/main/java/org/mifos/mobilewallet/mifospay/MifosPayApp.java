package org.mifos.mobilewallet.mifospay;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

import com.mifos.mobile.passcode.utils.ForegroundChecker;

import butterknife.ButterKnife;
import dagger.hilt.android.HiltAndroidApp;

/**
 * Created by naman on 17/8/17.
 */

@HiltAndroidApp
public class MifosPayApp extends Application {

    private static MifosPayApp instance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static MifosPayApp get(Context context) {
        return (MifosPayApp) context.getApplicationContext();
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (MifosPayApp.instance == null) {
            MifosPayApp.instance = this;
        }
        ButterKnife.setDebug(true);

        //Initialize ForegroundChecker
        ForegroundChecker.init(this);
    }
}

