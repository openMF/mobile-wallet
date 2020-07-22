package org.mifos.mobilewallet.mifospay;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

import com.mifos.mobile.passcode.utils.ForegroundChecker;

import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.injection.component.ApplicationComponent;
import org.mifos.mobilewallet.mifospay.injection.component.DaggerApplicationComponent;
import org.mifos.mobilewallet.mifospay.injection.module.ApplicationModule;
import org.mifos.mobilewallet.mifospay.utils.ThemeHelper;

import butterknife.ButterKnife;

/**
 * Created by naman on 17/8/17.
 */

public class MifosPayApp extends Application {

    private static MifosPayApp instance;
    private PreferencesHelper preferencesHelper;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    ApplicationComponent applicationComponent;

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

        // for applying application theme
        preferencesHelper = new PreferencesHelper(this);
        ThemeHelper.applyTheme(preferencesHelper.getApplicationTheme());
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

