package org.mifos.mobilewallet.mifospay.settings.ui;

import android.content.Intent;
import android.os.Bundle;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.settings.SettingsContract;
import org.mifos.mobilewallet.mifospay.settings.presenter.SettingsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * View class for settings
 * @author ankur
 * @since 9/7/18
 */
public class SettingsActivity extends BaseActivity implements SettingsContract.SettingsView {

    @Inject
    SettingsPresenter mPresenter;
    SettingsContract.SettingsPresenter mSettingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showBackButton();
        setToolbarTitle(Constants.SETTINGS);
        mPresenter.attachView(this);
    }

    /**
     * Attach presenter to view
     */
    @Override
    public void setPresenter(SettingsContract.SettingsPresenter presenter) {
        mSettingsPresenter = presenter;
    }

    /**
     * OnClickListener for logout
     */
    @OnClick(R.id.btn_logout)
    public void onLogoutClicked() {
        showProgressDialog(Constants.LOGGING_OUT);
        mSettingsPresenter.logout();
    }

    /**
     * OnClickListener for disable account
     */
    @OnClick(R.id.btn_disable_account)
    public void onDisableAccountClicked() {
        mSettingsPresenter.disableAccount();
    }

    /**
     * Method is called after user logs out from the Settings Activity
     */
    @Override
    public void startLoginActivity() {
        hideProgressDialog();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

