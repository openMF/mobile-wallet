package org.mifos.mobilewallet.mifospay.settings.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    @Override
    public void setPresenter(SettingsContract.SettingsPresenter presenter) {
        mSettingsPresenter = presenter;
    }

    @OnClick(R.id.btn_logout)
    public void onLogoutClicked() {
        showProgressDialog(Constants.LOGGING_OUT);
        mSettingsPresenter.logout();
    }

    @OnClick(R.id.btn_disable_account)
    public void onDisableAccountClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.disable_this_account));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        mSettingsPresenter.disableAccount();
    }

    @Override
    public void startLoginActivity() {
        hideProgressDialog();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
