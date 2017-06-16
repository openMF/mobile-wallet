package org.mifos.mobilewallet.core;

import android.support.v7.app.AppCompatActivity;

import org.mifos.mobilewallet.MifosWalletApp;
import org.mifos.mobilewallet.injection.component.ActivityComponent;
import org.mifos.mobilewallet.injection.component.DaggerActivityComponent;
import org.mifos.mobilewallet.injection.module.ActivityModule;

/**
 * Created by naman on 16/6/17.
 */

public class BaseActivity extends AppCompatActivity {

    private ActivityComponent activityComponent;

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(MifosWalletApp.get(this).component())
                    .build();
        }
        return activityComponent;
    }
}
