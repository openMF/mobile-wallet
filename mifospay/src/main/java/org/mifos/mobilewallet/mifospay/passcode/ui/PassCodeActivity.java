package org.mifos.mobilewallet.mifospay.passcode.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.mifos.mobile.passcode.MifosPassCodeActivity;
import com.mifos.mobile.passcode.utils.EncryptionUtil;
import com.mifos.mobile.passcode.utils.PassCodeConstants;
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import org.mifos.mobilewallet.mifospay.MifosPayApp;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.home.ui.MainActivity;
import org.mifos.mobilewallet.mifospay.injection.component.ActivityComponent;
import org.mifos.mobilewallet.mifospay.injection.component.DaggerActivityComponent;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;
import org.mifos.mobilewallet.mifospay.passcode.PassCodeContract;
import org.mifos.mobilewallet.mifospay.passcode.presenter.PassCodePresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity;
import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by ankur on 15/May/2018
 */
public class PassCodeActivity extends MifosPassCodeActivity implements
        PassCodeContract.PassCodeView {
    @Inject
    PassCodePresenter mPresenter;

    PassCodeContract.PassCodePresenter mPassCodePresenter;
    private String deepLinkURI = null;


    private ActivityComponent mActivityComponent;
    private String currPass = "";
    private Boolean updatePassword = false;
    private boolean isInitialScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // can't call getActivityComponent b/c PassCodeActivity class does not extend BaseActivity
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(MifosPayApp.get(this).component())
                .build();
        isInitialScreen = getIntent().getBooleanExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN,
                false);
        mActivityComponent.inject(this);
        if (getIntent() != null) {
            currPass = getIntent().getStringExtra(Constants.CURRENT_PASSCODE);
            updatePassword = getIntent().getBooleanExtra(Constants.UPDATE_PASSCODE, false);
        }
        ButterKnife.bind(this);
        deepLinkURI = getIntent().getStringExtra("uri");

    }

    @Override
    public int getLogo() {
        return 0;
    }

    @Override
    public void startNextActivity() {
        // authenticate user with saved Preferences
        mPresenter.createAuthenticatedService();

        if (deepLinkURI != null) {
            Uri uri = Uri.parse(deepLinkURI);
            Intent intent = new Intent(PassCodeActivity.this, ReceiptActivity.class);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Intent intent = new Intent(PassCodeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void startLoginActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PassCodeActivity.this);
        builder.setTitle(R.string.passcode_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PassCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showToaster(View view, int msg) {
        Toast.makeText(getApplicationContext(), getText(msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getEncryptionType() {
        return EncryptionUtil.DEFAULT;
    }

    @Override
    public void setPresenter(PassCodeContract.PassCodePresenter presenter) {
        mPassCodePresenter = mPresenter;
    }

    private void saveCurrentPasscode() {
        if (updatePassword && !currPass.isEmpty()) {
            PasscodePreferencesHelper helper = new PasscodePreferencesHelper(this);
            helper.savePassCode(currPass);
        }
    }

    @Override
    public void skip(View v) {
        saveCurrentPasscode();
        if (isInitialScreen) {
            startNextActivity();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveCurrentPasscode();
        finish();
    }

}
