package org.mifos.mobilewallet.mifospay.settings.ui;

import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.settings.SettingsContract;
import org.mifos.mobilewallet.mifospay.settings.presenter.SettingsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DialogBox;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity implements SettingsContract.SettingsView {
    public DialogBox dialogBox = new DialogBox();

    @Inject
    SettingsPresenter mPresenter;
    SettingsContract.SettingsPresenter mSettingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showColoredBackButton(Constants.BLACK_BACK_BUTTON);
        setToolbarTitle(Constants.SETTINGS);
        mPresenter.attachView(this);
    }

    @Override
    public void setPresenter(SettingsContract.SettingsPresenter presenter) {
        mSettingsPresenter = presenter;
    }

    @OnClick(R.id.btn_logout)
    public void onLogoutClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog);
        builder.setTitle(R.string.log_out_title);
        builder.setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProgressDialog(Constants.LOGGING_OUT);
                        mPresenter.logout();
                    }
                })
                .setNegativeButton(R.string.no, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.btn_disable_account)
    public void onDisableAccountClicked() {
        dialogBox.setOnPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSettingsPresenter.disableAccount();
            }
        });
        dialogBox.setOnNegativeListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBox.show(this, R.string.alert_disable_account,
                R.string.alert_disable_account_desc, R.string.ok, R.string.cancel);
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
