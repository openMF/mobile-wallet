package org.mifos.mobilewallet.mifospay;

import android.os.Bundle;

import org.mifos.mobilewallet.mifospay.base.BaseActivity;

public class AboutUs extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        setToolbarTitle(getString(R.string.about_us));
        showBackButton();
        replaceFragment(AboutUsFragment.newInstance(), false, R.id.container);
    }
}
