package org.mifos.mobilewallet.mifospay.editprofile.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.editprofile.EditProfileContract;
import org.mifos.mobilewallet.mifospay.editprofile.presenter.EditProfilePresenter;
import org.mifos.mobilewallet.mifospay.utils.AnimationUtil;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.TextDrawable;
import org.mifos.mobilewallet.mifospay.utils.Toaster;
import org.mifos.mobilewallet.mifospay.utils.ValidateUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity implements
        EditProfileContract.EditProfileView {

    private static final int REQUEST_READ_IMAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 7;
    @Inject
    EditProfilePresenter mPresenter;

    EditProfileContract.EditProfilePresenter mEditProfilePresenter;
    @BindView(R.id.iv_user_image)
    ImageView mIvUserImage;
    @BindView(R.id.tv_change_photo)
    TextView mTvChangePhoto;
    @BindView(R.id.et_current_password)
    EditText mEtCurrentPassword;
    @BindView(R.id.et_new_password)
    EditText mEtNewPassword;
    @BindView(R.id.et_confirm_password)
    EditText mEtConfirmPassword;
    @BindView(R.id.btn_password_cancel)
    Button mBtnPasswordCancel;
    @BindView(R.id.btn_password_save)
    Button mBtnPasswordSave;
    @BindView(R.id.ll_password)
    LinearLayout mLlPassword;
    @BindView(R.id.cv_password)
    CardView mCvPassword;
    @BindView(R.id.et_current_passcode)
    EditText mEtCurrentPasscode;
    @BindView(R.id.et_new_passcode)
    EditText mEtNewPasscode;
    @BindView(R.id.et_confirm_passcode)
    EditText mEtConfirmPasscode;
    @BindView(R.id.btn_passcode_cancel)
    Button mBtnPasscodeCancel;
    @BindView(R.id.btn_pasccode_save)
    Button mBtnPasccodeSave;
    @BindView(R.id.ll_passcode)
    LinearLayout mLlPasscode;
    @BindView(R.id.cv_passcode)
    CardView mCvPasscode;
    @BindView(R.id.tv_current_email)
    TextView mTvCurrentEmail;
    @BindView(R.id.et_new_email)
    EditText mEtNewEmail;
    @BindView(R.id.btn_email_cancel)
    Button mBtnEmailCancel;
    @BindView(R.id.btn_email_save)
    Button mBtnEmailSave;
    @BindView(R.id.ll_email)
    LinearLayout mLlEmail;
    @BindView(R.id.cv_email)
    CardView mCvEmail;
    @BindView(R.id.tv_current_mobile_number)
    TextView mTvCurrentMobileNumber;
    @BindView(R.id.ccp_new_code)
    CountryCodePicker mCcpNewCode;
    @BindView(R.id.et_new_mobile_number)
    EditText mEtNewMobileNumber;
    @BindView(R.id.btn_mobile_cancel)
    Button mBtnMobileCancel;
    @BindView(R.id.btn_mobile_save)
    Button mBtnMobileSave;
    @BindView(R.id.ll_mobile)
    LinearLayout mLlMobile;
    @BindView(R.id.cv_mobile)
    CardView mCvMobile;
    private String email;
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        showBackButton();
        setToolbarTitle(Constants.EDIT_PROFILE);
        mPresenter.attachView(this);

        showProgressDialog(Constants.PLEASE_WAIT);
        mEditProfilePresenter.fetchUserDetails();

        mCcpNewCode.registerCarrierNumberEditText(mEtNewMobileNumber);
    }

    @Override
    public void setPresenter(EditProfileContract.EditProfilePresenter presenter) {
        mEditProfilePresenter = presenter;
    }

    @OnClick(R.id.cv_password)
    public void onMCvPasswordClicked() {
        if (mLlPassword.isShown()) {
            AnimationUtil.collapse(mLlPassword);
        } else {
            // collapse other views
            if (mLlPasscode.isShown()) {
                AnimationUtil.collapse(mLlPasscode);
            } else if (mLlEmail.isShown()) {
                AnimationUtil.collapse(mLlEmail);
                mTvCurrentEmail.setVisibility(View.VISIBLE);
            } else if (mLlMobile.isShown()) {
                AnimationUtil.collapse(mLlMobile);
                mTvCurrentMobileNumber.setVisibility(View.VISIBLE);
            }
            AnimationUtil.expand(mLlPassword);
        }
    }

    @OnClick(R.id.cv_passcode)
    public void onMCvPasscodeClicked() {
        if (mLlPasscode.isShown()) {
            AnimationUtil.collapse(mLlPasscode);
        } else {
            // collapse other views
            if (mLlPassword.isShown()) {
                AnimationUtil.collapse(mLlPassword);
            } else if (mLlEmail.isShown()) {
                AnimationUtil.collapse(mLlEmail);
                mTvCurrentEmail.setVisibility(View.VISIBLE);
            } else if (mLlMobile.isShown()) {
                AnimationUtil.collapse(mLlMobile);
                mTvCurrentMobileNumber.setVisibility(View.VISIBLE);
            }
            AnimationUtil.expand(mLlPasscode);
        }
    }

    @OnClick(R.id.cv_email)
    public void onMCvEmailClicked() {
        if (mLlEmail.isShown()) {
            AnimationUtil.collapse(mLlEmail);
            mTvCurrentEmail.setVisibility(View.VISIBLE);
        } else {
            // collapse other views
            if (mLlPasscode.isShown()) {
                AnimationUtil.collapse(mLlPasscode);
            } else if (mLlPassword.isShown()) {
                AnimationUtil.collapse(mLlPassword);
            } else if (mLlMobile.isShown()) {
                AnimationUtil.collapse(mLlMobile);
                mTvCurrentMobileNumber.setVisibility(View.VISIBLE);
            }
            AnimationUtil.expand(mLlEmail);
            mTvCurrentEmail.setVisibility(View.GONE);
            mEtNewEmail.setText(email);
        }
    }

    @OnClick(R.id.cv_mobile)
    public void onMCvMobileClicked() {
        if (mLlMobile.isShown()) {
            AnimationUtil.collapse(mLlMobile);
            mTvCurrentMobileNumber.setVisibility(View.VISIBLE);
        } else {
            // collapse other views
            if (mLlPasscode.isShown()) {
                AnimationUtil.collapse(mLlPasscode);
            } else if (mLlPassword.isShown()) {
                AnimationUtil.collapse(mLlPassword);
            } else if (mLlEmail.isShown()) {
                AnimationUtil.collapse(mLlEmail);
                mTvCurrentEmail.setVisibility(View.VISIBLE);
            }
            AnimationUtil.expand(mLlMobile);
            mTvCurrentMobileNumber.setVisibility(View.GONE);
            mEtNewMobileNumber.setText(mobile);
        }
    }


    @OnClick({R.id.btn_password_cancel, R.id.btn_password_save, R.id.btn_passcode_cancel,
            R.id.btn_pasccode_save, R.id.btn_email_cancel, R.id.btn_email_save,
            R.id.btn_mobile_cancel, R.id.btn_mobile_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_password_cancel:
                if (mLlPassword.isShown()) {
                    AnimationUtil.collapse(mLlPassword);
                }
                break;

            case R.id.btn_passcode_cancel:
                if (mLlPasscode.isShown()) {
                    AnimationUtil.collapse(mLlPasscode);
                }
                break;

            case R.id.btn_email_cancel:
                if (mLlEmail.isShown()) {
                    AnimationUtil.collapse(mLlEmail);
                }
                break;

            case R.id.btn_mobile_cancel:
                if (mLlMobile.isShown()) {
                    AnimationUtil.collapse(mLlMobile);
                }
                break;

            case R.id.btn_password_save:
                showProgressDialog(Constants.PLEASE_WAIT);
                if (mEtNewPassword.getText().toString().equals(
                        mEtConfirmPassword.getText().toString())) {
                    mEditProfilePresenter.updatePassword(mEtCurrentPassword.getText().toString(),
                            mEtNewPassword.getText().toString());
                } else {
                    hideProgressDialog();
                    showToast("Password mismatch");
                }
                break;

            case R.id.btn_pasccode_save:
                showProgressDialog(Constants.PLEASE_WAIT);
                if (mEtNewPasscode.getText().toString().equals(
                        mEtConfirmPasscode.getText().toString())) {
                    mEditProfilePresenter.updatePasscode(mEtCurrentPasscode.getText().toString(),
                            mEtNewPasscode.getText().toString());
                } else {
                    hideProgressDialog();
                    showToast("Passcode mismatch");
                }
                break;

            case R.id.btn_email_save:
                showProgressDialog(Constants.PLEASE_WAIT);
                if (ValidateUtil.validateEmail(mEtNewEmail.getText().toString())) {
                    mEditProfilePresenter.updateEmail(mEtNewEmail.getText().toString());
                } else {
                    hideProgressDialog();
                    showToast("Incorrect email address");
                }
                break;

            case R.id.btn_mobile_save:
                showProgressDialog(Constants.PLEASE_WAIT);
                if (mCcpNewCode.isValidFullNumber()) {
                    mEditProfilePresenter.updateMobile(mCcpNewCode.getFullNumber());
                } else {
                    hideProgressDialog();
                    showToast("Incorrect mobile number");
                }
                break;
        }
    }

    @OnClick(R.id.tv_change_photo)
    public void onChangePhotoClicked() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        } else {

            // Permission has already been granted
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, REQUEST_READ_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.

                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, REQUEST_READ_IMAGE);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showToast(Constants.NEED_EXTERNAL_STORAGE_PERMISSION_TO_BROWSE_IMAGES);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_READ_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            mIvUserImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            // TODO: update image on server
        }
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    public void onUpdateEmailSuccess(String email) {
        hideProgressDialog();
        this.email = email;
        mTvCurrentEmail.setText(email);
        AnimationUtil.collapse(mLlEmail);
        mTvCurrentEmail.setVisibility(View.VISIBLE);
        showToast("Email successfully updated");
    }

    @Override
    public void onUpdateEmailError(String message) {
        hideProgressDialog();
        showToast(message);
    }

    @Override
    public void onUpdateMobileSuccess(String fullNumber) {
        hideProgressDialog();
        mobile = fullNumber;
        mTvCurrentMobileNumber.setText(fullNumber);
        AnimationUtil.collapse(mLlMobile);
        mTvCurrentMobileNumber.setVisibility(View.VISIBLE);
        showToast("Mobile number successfully updated");
    }

    @Override
    public void onUpdateMobileError(String message) {
        hideProgressDialog();
        showToast(message);
    }

    @Override
    public void onUpdatePasswordSuccess() {
        hideProgressDialog();
        AnimationUtil.collapse(mLlPassword);
        showToast("Password successfully updated");
    }

    @Override
    public void onUpdatePasswordError(String message) {
        hideProgressDialog();
        showToast(message);
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
        mTvCurrentEmail.setText(email);
    }

    @Override
    public void setMobile(String mobile) {
        this.mobile = mobile;
        mTvCurrentMobileNumber.setText(mobile);
    }

    @Override
    public void setImage(String fullName) {
        TextDrawable drawable = TextDrawable.builder().buildRound(fullName.substring(0, 1),
                R.color.colorPrimary);
        mIvUserImage.setImageDrawable(drawable);
        hideProgressDialog();
    }
}
