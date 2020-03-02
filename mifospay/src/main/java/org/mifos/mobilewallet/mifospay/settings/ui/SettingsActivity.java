package org.mifos.mobilewallet.mifospay.settings.ui;

import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.home.ui.HomeFragment;
import org.mifos.mobilewallet.mifospay.utils.Constants;

public class SettingsActivity extends BaseActivity {

    private boolean hasSettingsChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbarTitle(getString(R.string.settings));
        showBackButton();
        replaceFragment(SettingsFragment.newInstance(), false, R.id.container);
        if (getIntent().hasExtra(Constants.HAS_SETTINGS_CHANGED)) {
            hasSettingsChanged = getIntent().getBooleanExtra(Constants.HAS_SETTINGS_CHANGED,
                    false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (hasSettingsChanged) {
            ActivityCompat.finishAffinity(this);
            Intent i = new Intent(this, HomeFragment.class);
            startActivity(i);
        }
    }
}


