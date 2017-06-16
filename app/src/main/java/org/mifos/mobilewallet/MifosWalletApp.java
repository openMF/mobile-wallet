package org.mifos.mobilewallet;

import android.app.Application;
import android.content.Context;

import org.mifos.mobilewallet.injection.component.ApplicationComponent;
import org.mifos.mobilewallet.injection.component.DaggerApplicationComponent;
import org.mifos.mobilewallet.injection.module.ApplicationModule;

import butterknife.ButterKnife;

/**
 * Created by naman on 16/6/17.
 */

public class MifosWalletApp extends Application {

    ApplicationComponent applicationComponent;

    private static MifosWalletApp instance;

    public static MifosWalletApp get(Context context) {
        return (MifosWalletApp) context.getApplicationContext();
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ButterKnife.setDebug(true);
    }

    public ApplicationComponent component() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return applicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

}
