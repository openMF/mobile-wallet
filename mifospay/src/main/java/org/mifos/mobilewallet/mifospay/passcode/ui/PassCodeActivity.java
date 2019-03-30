package org.mifos.mobilewallet.mifospay.passcode.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mifos.mobile.passcode.MifosPassCodeActivity;
import com.mifos.mobile.passcode.utils.EncryptionUtil;

import org.mifos.mobilewallet.mifospay.MifosPayApp;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.home.ui.MainActivity;
import org.mifos.mobilewallet.mifospay.injection.component.ActivityComponent;
import org.mifos.mobilewallet.mifospay.injection.component.DaggerActivityComponent;
import org.mifos.mobilewallet.mifospay.injection.module.ActivityModule;
import org.mifos.mobilewallet.mifospay.passcode.PassCodeContract;
import org.mifos.mobilewallet.mifospay.passcode.presenter.PassCodePresenter;

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

    private ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // can't call getActivityComponent b/c PassCodeActivity class does not extend BaseActivity
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(MifosPayApp.get(this).component())
                .build();

        mActivityComponent.inject(this);

        ButterKnife.bind(this);

    }

    @Override
    public int getLogo() {
        return 0;
    }

    @Override
    public void startNextActivity() {
        // authenticate user with saved Preferences
        mPresenter.createAuthenticatedService();

        Intent intent = new Intent(PassCodeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(PassCodeActivity.this, LoginActivity.class);
        startActivity(intent);
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


}
