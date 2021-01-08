package org.mifos.mobilewallet.mifospay.performchecks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.scottyab.rootbeer.RootBeer;

import org.mifos.mobilewallet.mifospay.securitychecks.ui.SecurityChecksActivity;

/**
 * Created by Harshad Dabhade on 8/1/2021
 */

public class PerformChecks extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RootBeer rootBeer = new RootBeer(getApplicationContext());

        //Checking device has root access or not.
        if (rootBeer.isRooted()) {
            startActivity(new Intent(PerformChecks.this, SecurityChecksActivity.class));
        }
        return flags;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}