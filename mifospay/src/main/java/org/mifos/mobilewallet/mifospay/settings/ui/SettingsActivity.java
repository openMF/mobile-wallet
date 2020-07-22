package org.mifos.mobilewallet.mifospay.settings.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.settings.SettingsContract;
import org.mifos.mobilewallet.mifospay.settings.presenter.SettingsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DialogBox;
import org.mifos.mobilewallet.mifospay.utils.ThemeHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity implements SettingsContract.SettingsView {
    public DialogBox dialogBox = new DialogBox();

    @Inject
    SettingsPresenter mPresenter;
    SettingsContract.SettingsPresenter mSettingsPresenter;

    @Inject
    PreferencesHelper preferencesHelper;

    AlertDialog alertDialog;

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

    @OnClick(R.id.cv_select_theme)
    public void showThemeDialogBox(View v) {
        Context wrapper = new ContextThemeWrapper(this, R.style.AppTheme_Dialog);
        AlertDialog.Builder themeDialog = new AlertDialog.Builder(wrapper);
        themeDialog.setTitle(R.string.select_theme);
        String[] themes = {getString(R.string.light_theme),
                getString(R.string.dark_theme),
                getString(R.string.system_theme)};
        String currentTheme = preferencesHelper.getApplicationTheme();

        int checkedItem = 2;
        switch (currentTheme) {
            case Constants.LIGHT_THEME:
                checkedItem = 0;
                break;
            case Constants.DARK_THEME:
                checkedItem = 1;
                break;
        }

        themeDialog.setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
            switch (which) {
                case 0:
                    preferencesHelper.setApplicationTheme(Constants.LIGHT_THEME);
                    applyChanges(Constants.LIGHT_THEME);
                    break;
                case 1:
                    preferencesHelper.setApplicationTheme(Constants.DARK_THEME);
                    applyChanges(Constants.DARK_THEME);
                    break;
                case 2:
                    preferencesHelper.setApplicationTheme(Constants.SYSTEM_THEME);
                    applyChanges(Constants.SYSTEM_THEME);
                    break;
            }
        });
        alertDialog = themeDialog.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private void applyChanges(String theme) {
        alertDialog.dismiss();
        ThemeHelper.applyTheme(theme);
        finish();
    }
}
