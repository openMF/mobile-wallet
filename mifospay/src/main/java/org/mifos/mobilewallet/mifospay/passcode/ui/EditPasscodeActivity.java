package org.mifos.mobilewallet.mifospay.passcode.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mobile.passcode.MifosPassCodeView;
import com.mifos.mobile.passcode.utils.EncryptionUtil;
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;

import org.mifos.mobilewallet.mifospay.R;


public class EditPasscodeActivity extends AppCompatActivity implements MifosPassCodeView.
        PassCodeListener {

    NestedScrollView clRootview;
    AppCompatButton btnForgotPasscode;
    MifosPassCodeView mifosPassCodeView;
    AppCompatButton btnSkip;
    Button btnSave;
    TextView tvPasscodeIntro;
    ImageView ivVisibility;
    private PasscodePreferencesHelper passcodePreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_code);

        clRootview = findViewById(R.id.cl_rootview);
        btnForgotPasscode = findViewById(R.id.btn_forgot_passcode);
        mifosPassCodeView = findViewById(R.id.pv_passcode);
        btnSkip = findViewById(R.id.btn_skip);
        btnSkip.setText(getString(R.string.cancel));
        btnSave = findViewById(R.id.btn_save);
        btnSave.setText(getString(R.string.ok));
        tvPasscodeIntro = findViewById(R.id.tv_passcode);
        tvPasscodeIntro.setText(getString(R.string.enter_passcode));
        ivVisibility = findViewById(R.id.iv_visibility);
        passcodePreferencesHelper = new PasscodePreferencesHelper(this);
    }

    private String encryptPassCode(String passCode) {
        String encryptedPassCode = EncryptionUtil.getMobileBankingHash(passCode);
        return encryptedPassCode;
    }


    public void savePassCode(View view) {
        if (isPassCodeLengthCorrect()) {
            if (encryptPassCode(mifosPassCodeView.getPasscode()).equals(passcodePreferencesHelper
                    .getPassCode())
            ) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.incorrect_passcode),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.invalid_passcode),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void passCodeEntered(String passcode) { }

    public void clickedOne(View v) {
        mifosPassCodeView.enterCode(getString(R.string.one));
    }

    public void clickedTwo(View v) {
        mifosPassCodeView.enterCode(getString(R.string.two));
    }

    public void clickedThree(View v) {
        mifosPassCodeView.enterCode(getString(R.string.three));
    }

    public void clickedFour(View v) {
        mifosPassCodeView.enterCode(getString(R.string.four));
    }

    public void clickedFive(View v) {
        mifosPassCodeView.enterCode(getString(R.string.five));
    }

    public void clickedSix(View v) {
        mifosPassCodeView.enterCode(getString(R.string.six));
    }

    public void clickedSeven(View v) {
        mifosPassCodeView.enterCode(getString(R.string.seven));
    }

    public void clickedEight(View v) {
        mifosPassCodeView.enterCode(getString(R.string.eight));
    }

    public void clickedNine(View v) {
        mifosPassCodeView.enterCode(getString(R.string.nine));
    }

    public void clickedZero(View v) {
        mifosPassCodeView.enterCode(getString(R.string.zero));
    }

    public void clickedBackSpace(View v) {
        mifosPassCodeView.backSpace();
    }

    public void skip(View v) {
        finish();
    }

    /**
     * @param view PasscodeView that changes to text if it was hidden and vice a versa
     */
    public void visibilityChange(View view) {
        mifosPassCodeView.revertPassCodeVisibility();
        if (!mifosPassCodeView.passcodeVisible()) {
            ivVisibility.setColorFilter(
                    ContextCompat.getColor(EditPasscodeActivity.this,
                            R.color.light_grey));
        } else {
            ivVisibility.setColorFilter(ContextCompat.getColor(
                    EditPasscodeActivity.this,
                    R.color.gray_dark));
        }
    }

    /**
     * Checks whether passcode entered is of correct length
     *
     * @return Returns true if passcode lenght is 4 else shows message
     */
    private boolean isPassCodeLengthCorrect() {
        if (mifosPassCodeView.getPasscode().length() == 4) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
