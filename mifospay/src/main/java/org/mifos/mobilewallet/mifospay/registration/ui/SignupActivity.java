package org.mifos.mobilewallet.mifospay.registration.ui;

import static org.mifos.mobilewallet.mifospay.utils.FileUtils.readJson;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mifos.mobile.passcode.utils.PassCodeConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.auth.ui.LoginActivity;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;
import org.mifos.mobilewallet.mifospay.registration.presenter.SignupPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class SignupActivity extends BaseActivity implements RegistrationContract.SignupView {

    @Inject
    SignupPresenter mPresenter;

    RegistrationContract.SignupPresenter mSignupPresenter;
    @BindView(R.id.et_first_name)
    EditText mEtFirstName;
    @BindView(R.id.et_last_name)
    EditText mEtLastName;
    @BindView(R.id.et_email)
    EditText mEtEmail;
    @BindView(R.id.et_business_shop_name)
    EditText mEtBusinessShopName;
    @BindView(R.id.et_address_line_1)
    EditText mEtAddressLine1;
    @BindView(R.id.et_address_line_2)
    EditText mEtAddressLine2;
    @BindView(R.id.et_pin_code)
    EditText mEtPinCode;
    @BindView(R.id.et_state)
    EditText mEtCity;
    @BindView(R.id.fab_next)
    FloatingActionButton mFabNext;

    SpinnerDialog spinnerDialog;
    @BindView(R.id.et_user_name)
    EditText mEtUserName;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_confirm_password)
    EditText mEtConfirmPassword;

    private String countryName;
    private String mobileNumber;
    private String countryId;
    private String stateId;
    private int mifosSavingProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getActivityComponent().inject(this);
        ButterKnife.bind(this);

        mPresenter.attachView(this);

        showBackButton();
        setToolbarTitle("");

        mifosSavingProductId = getIntent().getIntExtra(Constants.MIFOS_SAVINGS_PRODUCT_ID, 0);
        if (mifosSavingProductId
                == org.mifos.mobilewallet.core.utils.Constants.MIFOS_MERCHANT_SAVINGS_PRODUCT_ID) {
            mEtBusinessShopName.setVisibility(View.VISIBLE);
        } else {
            mEtBusinessShopName.setVisibility(View.GONE);
        }
        mobileNumber = getIntent().getStringExtra(Constants.MOBILE_NUMBER);
        countryName = getIntent().getStringExtra(Constants.COUNTRY);

        String email = getIntent().getStringExtra(Constants.GOOGLE_EMAIL);
        String displayName = getIntent().getStringExtra(Constants.GOOGLE_DISPLAY_NAME);
        String firstName = getIntent().getStringExtra(Constants.GOOGLE_GIVEN_NAME);
        String lastName = getIntent().getStringExtra(Constants.GOOGLE_FAMILY_NAME);
        Uri photoUri = getIntent().getParcelableExtra(Constants.GOOGLE_PHOTO_URI);

        if (displayName != null) {
            mEtBusinessShopName.setText(displayName);
        }
        if (email != null) {
            mEtEmail.setText(email);
            mEtUserName.setText(email.substring(0, email.indexOf('@')));
        }
        if (firstName != null) {
            mEtFirstName.setText(firstName);
        }
        if (lastName != null) {
            mEtLastName.setText(lastName);
        }

        DebugUtil.log(mobileNumber, countryName, email, displayName, firstName, lastName, photoUri);

        showProgressDialog(Constants.PLEASE_WAIT);

        initSearchableStateSpinner();

//        mEtFirstName.setText("Harry");
//        mEtLastName.setText("Potter");
//        mEtUserName.setText("harrypotter123");
//        mEtPassword.setText("harryharry");
//        mEtConfirmPassword.setText("harryharry");
//        mEtEmail.setText("ankurs287@gmail.com");
//        mEtBusinessShopName.setText("Hogwarts");
//        mEtAddressLine1.setText("diagon");
//        mEtAddressLine2.setText("alley");
//        mEtPinCode.setText("111222");
//        mEtCity.setText("Delhi");
    }

    private void initSearchableStateSpinner() {
        JSONObject jsonObject = null;
        try {
            countryId = "";
            jsonObject = readJson(this, "countries.json");
            JSONArray countriesArray = jsonObject.getJSONArray("countries");
            for (int i = 0; i < countriesArray.length(); i++) {
                if (countriesArray.getJSONObject(i).getString("name").equals(countryName)) {
                    countryId = countriesArray.getJSONObject(i).getString("id");
                    break;
                }
            }

            jsonObject = readJson(this, "states.json");
            JSONArray statesJson = jsonObject.getJSONArray("states");
            ArrayList<String> statesList = new ArrayList<>();
            for (int i = 0; i < statesJson.length(); i++) {
                JSONObject statesJsonObject = statesJson.getJSONObject(i);
                if (statesJsonObject.getString("country_id").equals(countryId)) {
                    statesList.add(statesJsonObject.getString("name"));
                    stateId = statesJsonObject.getString("id");
                }
            }

            spinnerDialog = new SpinnerDialog(SignupActivity.this, statesList,
                    "Select or Search State", R.style.DialogAnimations_SmileWindow, "Close");

            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    mEtCity.setText(item);
                }
            });

            mEtCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerDialog.showSpinerDialog();
                }
            });

            hideProgressDialog();

        } catch (Exception e) {
            Log.d("qxz", e.toString() + " " + e.getMessage());
        }
    }

    @Override
    public void setPresenter(RegistrationContract.SignupPresenter presenter) {
        mSignupPresenter = presenter;
    }

    @OnClick(R.id.fab_next)
    public void onNextClicked() {
        showProgressDialog(Constants.PLEASE_WAIT);

        if (isEmpty(mEtFirstName) || isEmpty(mEtLastName) || isEmpty(mEtEmail) || isEmpty(
                mEtBusinessShopName) || isEmpty(mEtAddressLine1) || isEmpty(mEtAddressLine2)
                || isEmpty(mEtPinCode) || isEmpty(mEtCity) || isEmpty(mEtUserName) || isEmpty(
                mEtPassword) || isEmpty(mEtConfirmPassword)) {
            Toaster.showToast(this, "All fields are mandatory");
            hideProgressDialog();
            return;
        }

        String firstName = mEtFirstName.getText().toString().trim();
        String lastName = mEtLastName.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        String businessName = mEtBusinessShopName.getText().toString().trim();
        String addressline1 = mEtAddressLine1.getText().toString().trim();
        String addressline2 = mEtAddressLine2.getText().toString().trim();
        String pincode = mEtPinCode.getText().toString().trim();
        String city = mEtCity.getText().toString().trim();
        String username = mEtUserName.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String confirmPassword = mEtConfirmPassword.getText().toString().trim();

        if (!password.equals(confirmPassword)) {
            Toaster.showToast(this, "Password is not same as Confirm Password");
            hideProgressDialog();
            return;
        }

        mSignupPresenter.registerUser(firstName, lastName, mobileNumber, email, businessName,
                addressline1, addressline2, pincode, city, countryName, username, password, stateId,
                countryId, mifosSavingProductId);
    }

    @Override
    public void onRegisterSuccess(String s) {
        // registered but unable to login or user not updated with client
        // TODO :: Consider this case
        // 1. User not updated: when logging in update user
        // 2. User unable to login (must be caused due to server)
        hideProgressDialog();
        showToast("Registered successfully.");
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void loginSuccess() {
        hideProgressDialog();
        showToast("Registered successfully");

        Intent intent = new Intent(SignupActivity.this, PassCodeActivity.class);
        intent.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterFailed(String message) {
        hideProgressDialog();
        showToast(message);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void showToast(String s) {
        Toaster.showToast(this, s);
    }
}
