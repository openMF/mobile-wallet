package org.mifos.mobilewallet.mifospay.settings.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;
import org.mifos.mobilewallet.mifospay.password.ui.EditPasswordActivity;
import org.mifos.mobilewallet.mifospay.settings.SettingsContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DialogBox;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragmentCompat {
    public DialogBox dialogBox = new DialogBox();
    @Inject
    SettingsContract.SettingsPresenter mSettingsPresenter;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }



    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case Constants.PASSWORD:
                startActivity(new Intent(getContext(), EditPasswordActivity.class));
                break;
            case Constants.PASSCODE:
                startActivity(new Intent(getContext(), PassCodeActivity.class));
                break;
            case Constants.DISABLE_ACCOUNT:
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
                dialogBox.show(getContext(), R.string.alert_disable_account,
                        R.string.alert_disable_account_desc, R.string.ok, R.string.cancel);
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }
}
