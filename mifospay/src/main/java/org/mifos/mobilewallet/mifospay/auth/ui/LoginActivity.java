package org.mifos.mobilewallet.mifospay.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mifos.mobile.passcode.utils.PassCodeConstants;
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.AuthContract;
import org.mifos.mobilewallet.mifospay.auth.presenter.LoginPresenter;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;
import org.mifos.mobilewallet.mifospay.registration.ui.MobileVerificationActivity;
import org.mifos.mobilewallet.mifospay.registration.ui.SignupMethod;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.Toaster;
import org.mifos.mobilewallet.mifospay.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by naman on 16/6/17.
 */

public class LoginActivity extends BaseActivity implements AuthContract.LoginView {

    @Inject
    LoginPresenter mPresenter;

    AuthContract.LoginPresenter mLoginPresenter;

    @BindView(R.id.et_username)
    TextInputEditText etUsername;

    @BindView(R.id.et_password)
    TextInputEditText etPassword;

    @BindView(R.id.btn_login)
    Button btnLogin;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;
    private int mMifosSavingProductId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        PasscodePreferencesHelper pref = new PasscodePreferencesHelper(getApplicationContext());
        if (!pref.getPassCode().isEmpty()) {
            startPassCodeActivity();
        }

        disableLoginButton();
    }

    @Override
    public void setPresenter(AuthContract.LoginPresenter presenter) {
        mLoginPresenter = presenter;
    }

    @OnFocusChange({R.id.et_username, R.id.et_password})
    public void onLoginInputFocusChanged() {
        handleLoginInputChanged();
    }

    @OnTextChanged({R.id.et_username, R.id.et_password})
    public void onLoginInputTextChanged() {
        handleLoginInputChanged();
    }

    private void handleLoginInputChanged() {
        String usernameContent = etUsername.getText().toString();
        String passwordContent = etPassword.getText().toString();
        mPresenter.handleLoginButtonStatus(usernameContent, passwordContent);
    }

    @OnClick(R.id.btn_login)
    public void onLoginClicked() {
        Utils.hideSoftKeyboard(this);
        showProgressDialog(Constants.LOGGING_IN);
        mLoginPresenter.loginUser(etUsername.getText().toString(),
                etPassword.getText().toString());
    }

    @OnClick(R.id.ll_signup)
    public void onSignupClicked() {
        SignupMethod signupMethod = new SignupMethod();
        signupMethod.show(getSupportFragmentManager(), Constants.CHOOSE_SIGNUP_METHOD);
    }

    @OnClick(R.id.bg_screen)
    public void backgroundScreenClicked() {
        if (this.getCurrentFocus() != null) {
            Utils.hideSoftKeyboard(this);
        }
    }

    @Override
    public void disableLoginButton() {
        btnLogin.setEnabled(false);
    }

    @Override
    public void enableLoginButton() {
        btnLogin.setEnabled(true);
    }

    @Override
    public void loginSuccess() {
        hideProgressDialog();
        Utils.hideSoftKeyboard(this);
        startPassCodeActivity();
    }

    @Override
    public void loginFail(String message) {
        Utils.hideSoftKeyboard(this);
        hideProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts {@link PassCodeActivity} with {@code Constans.INTIAL_LOGIN} as true
     */
    private void startPassCodeActivity() {
        Intent intent = new Intent(LoginActivity.this, PassCodeActivity.class);
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true);
        startActivity(intent);
    }

    public void signupUsingGoogleAccount(int mifosSavingsProductId) {
        showProgressDialog(Constants.PLEASE_WAIT);

        mMifosSavingProductId = mifosSavingsProductId;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = googleSignInClient.getSignInIntent();

        hideProgressDialog();
        startActivityForResult(signInIntent, 11);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        showProgressDialog(Constants.PLEASE_WAIT);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 11) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                hideProgressDialog();
                signup(mMifosSavingProductId);

            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                DebugUtil.log(Constants.GOOGLE_SIGN_IN_FAILED, e.getMessage());
                Toaster.showToast(this, Constants.GOOGLE_SIGN_IN_FAILED);
                hideProgressDialog();
            }
        }
    }

    public void signup(int mifosSavingsProductId) {
        showProgressDialog(Constants.PLEASE_WAIT);
        Intent intent = new Intent(LoginActivity.this, MobileVerificationActivity.class);
        mMifosSavingProductId = mifosSavingsProductId;
        intent.putExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, mMifosSavingProductId);
        if (account != null) {
            intent.putExtra(Constants.GOOGLE_PHOTO_URI, account.getPhotoUrl());
            intent.putExtra(Constants.GOOGLE_DISPLAY_NAME, account.getDisplayName());
            intent.putExtra(Constants.GOOGLE_EMAIL, account.getEmail());
            intent.putExtra(Constants.GOOGLE_FAMILY_NAME, account.getFamilyName());
            intent.putExtra(Constants.GOOGLE_GIVEN_NAME, account.getGivenName());
        }
        hideProgressDialog();

        startActivity(intent);

        if (googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            account = null;
                        }
                    });
        }
    }
}
