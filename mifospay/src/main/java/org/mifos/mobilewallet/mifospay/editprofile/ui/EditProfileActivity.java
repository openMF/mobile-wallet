package org.mifos.mobilewallet.mifospay.editprofile.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.hbb20.CountryCodePicker;
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper;
import com.yalantis.ucrop.UCrop;

import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.editprofile.EditProfileContract;
import org.mifos.mobilewallet.mifospay.editprofile.presenter.EditProfilePresenter;
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity;
import org.mifos.mobilewallet.mifospay.password.ui.EditPasswordActivity;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DialogBox;
import org.mifos.mobilewallet.mifospay.utils.TextDrawable;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class EditProfileActivity extends BaseActivity implements
        EditProfileContract.EditProfileView {

    private static final int REQUEST_READ_IMAGE = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 7;

    @Inject
    EditProfilePresenter mPresenter;

    EditProfileContract.EditProfilePresenter mEditProfilePresenter;

    @BindView(R.id.ccp_new_code)
    CountryCodePicker ccpCountryCode;

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.til_edit_profile_username)
    TextInputLayout tilUsername;

    @BindView(R.id.til_edit_profile_email)
    TextInputLayout tilEmail;

    @BindView(R.id.til_edit_profile_vpa)
    TextInputLayout tilVpa;

    @BindView(R.id.til_edit_profile_mobile)
    TextInputLayout tilMobileNumber;

    @BindView(R.id.et_edit_profile_username)
    EditText etUsername;

    @BindView(R.id.et_edit_profile_email)
    EditText etEmail;

    @BindView(R.id.et_edit_profile_vpa)
    EditText etVpa;

    @BindView(R.id.et_edit_profile_mobile)
    EditText etMobileNumber;

    @BindView(R.id.fab_edit_profile_save_changes)
    FloatingActionButton fabSaveChanges;

    @BindViews({R.id.et_edit_profile_username, R.id.et_edit_profile_email,
            R.id.et_edit_profile_vpa, R.id.et_edit_profile_mobile})
    List<EditText> userDetailsInputs;

    private BottomSheetDialog bottomSheetDialog;
    public DialogBox dialogBox = new DialogBox();
    private PasscodePreferencesHelper passcodePreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getActivityComponent().inject(this);
        ButterKnife.bind(this);
        setupUi();
        mPresenter.attachView(this);
        ccpCountryCode.registerCarrierNumberEditText(etMobileNumber);
        mEditProfilePresenter.fetchUserDetails();
        passcodePreferencesHelper = new PasscodePreferencesHelper(this);
        if (isChangeImageRequestFromProfile()) {
            bottomSheetDialog.show();
        }
    }

    private void setupUi() {
        hideFab();
        showCloseButton();
        setToolbarTitle(Constants.EDIT_PROFILE);
        setupBottomSheetDialog();
    }

    private void setupBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(EditProfileActivity.this);
        View sheetView = getLayoutInflater()
                .inflate(R.layout.dialog_change_profile_picture, null);
        bottomSheetDialog.setContentView(sheetView);
        BottomSheetViews bsv = new BottomSheetViews();
        ButterKnife.bind(bsv, sheetView);
    }

    private boolean isChangeImageRequestFromProfile() {
        return getIntent().getStringExtra(Constants.CHANGE_PROFILE_IMAGE_KEY) != null;
    }

    @Override
    public void setPresenter(EditProfileContract.EditProfilePresenter presenter) {
        mEditProfilePresenter = presenter;
    }

    @OnClick(R.id.fab_edit_profile_save_changes)
    public void onSaveChangesClicked() {
        for (EditText input : userDetailsInputs) {
            if (isDataSaveNecessary(input)) {
                mPresenter.updateInputById(input.getId(), input.getText().toString());
            }
        }
        hideFab();
        hideKeyboard();
    }

    @OnClick(R.id.iv_user_image)
    public void onProfileImageClicked() {
        bottomSheetDialog.show();
    }

    @OnClick(R.id.btn_change_password)
    public void onChangePasswordClicked() {
        startActivity(new Intent(getApplicationContext(), EditPasswordActivity.class));
    }

    @OnClick(R.id.btn_change_passcode)
    public void onChangePasscodeClicked() {
        String currentPasscode = passcodePreferencesHelper.getPassCode();
        // for re-initiating passcode generation process
        passcodePreferencesHelper.savePassCode("");
        Intent intent = new Intent(this, PassCodeActivity.class );
        intent.putExtra(Constants.CURRENT_PASSCODE, currentPasscode);
        intent.putExtra(Constants.UPDATE_PASSCODE, true);
        startActivity(intent);
    }

    @OnTextChanged({R.id.et_edit_profile_username, R.id.et_edit_profile_email,
            R.id.et_edit_profile_vpa, R.id.et_edit_profile_mobile})
    public void onUserDetailsChanged() {
        for (EditText et : userDetailsInputs) {
            if (isDataSaveNecessary(et)) {
                mPresenter.handleNecessaryDataSave();
                break;
            } else {
                hideFab();
            }
        }
    }

    private boolean isDataSaveNecessary(EditText input) {
        String content = input.getText().toString();
        String currentContent = input.getHint().toString();
        return !(TextUtils.isEmpty(content) || content.equals(currentContent));
    }

    @OnFocusChange({R.id.et_edit_profile_username, R.id.et_edit_profile_email,
            R.id.et_edit_profile_vpa, R.id.et_edit_profile_mobile})
    public void onUserDetailsFocusChanged(EditText input, boolean isFocused) {
        if (!isDataSaveNecessary((input))) {
            if (isFocused) {
                input.setText(input.getHint().toString());
            } else {
                input.getText().clear();
            }
        }
    }

    @Override
    public void showDefaultImageByUsername(String fullName) {
        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .width((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .height((int) getResources().getDimension(R.dimen.user_profile_image_size))
                .endConfig().buildRound(fullName.substring(0, 1), R.color.colorPrimary);
        ivUserImage.setImageDrawable(drawable);
    }

    @Override
    public void showUsername(String username) {
        tilUsername.setHint(username);
        handleUpdatedInput(etUsername);
    }

    @Override
    public void showEmail(String email) {
        tilEmail.setHint(email);
        handleUpdatedInput(etEmail);
    }

    @Override
    public void showVpa(String vpa) {
        tilVpa.setHint(vpa);
        handleUpdatedInput(etVpa);
    }

    @Override
    public void showMobileNumber(String mobileNumber) {
        tilMobileNumber.setHint(mobileNumber);
        handleUpdatedInput(etMobileNumber);
    }

    private void handleUpdatedInput(EditText input) {
        input.clearFocus();
        input.getText().clear();
    }

    @Override
    public void showFab() {
        fabSaveChanges.show();
    }

    @Override
    public void hideFab() {
        fabSaveChanges.hide();
    }

    public void hideKeyboard() {
        InputMethodManager imm
                = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (imm != null) {
            if (view == null) {
                view = new View(this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_READ_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    startCrop(selectedImageUri);
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = UUID.randomUUID().toString() + ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = getConfiguredUCrop(uCrop);
        uCrop.start(EditProfileActivity.this);
    }

    private UCrop getConfiguredUCrop(UCrop uCrop) {
        uCrop = uCrop.withAspectRatio(1, 1);

        int size = (int) getResources().getDimension(R.dimen.user_profile_image_size);
        if (size > UCrop.MIN_SIZE) {
            uCrop = uCrop.withMaxResultSize(size, size);
        }

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);

        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.primaryBlue));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridColumnCount(0);
        options.setCropGridRowCount(0);
        return uCrop.withOptions(options);
    }

    @Override
    public void removeProfileImage() {
        // TODO: Remove image from database
    }

    @Override
    public void changeProfileImage() {
        pickImageFromGallery();
    }

    private void pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            startActivityForResult(Intent.createChooser(i,
                    getString(R.string.label_select_picture)), REQUEST_READ_IMAGE);
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            ivUserImage.setImageURI(resultUri);
        }
    }

    @Override
    public void showDiscardChangesDialog() {
        dialogBox.setOnPositiveListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.onDialogPositive();
                dialog.dismiss();
            }
        });
        dialogBox.setOnNegativeListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.onDialogNegative();
                dialog.dismiss();
            }
        });
        dialogBox.show(this, R.string.discard_changes_and_exit,
                R.string.discard_changes_and_exit_description, R.string.accept, R.string.cancel);
    }

    @Override
    public void onUpdateEmailError(String message) {
        showToast(message);
    }

    @Override
    public void onUpdateMobileError(String message) {
        showToast(message);
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(this, message);
    }

    @Override
    public void startProgressBar() {
        showProgressDialog(Constants.PLEASE_WAIT);
    }

    @Override
    public void stopProgressBar() {
        hideProgressDialog();
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (fabSaveChanges.isOrWillBeShown()) {
            mPresenter.handleExitOnUnsavedChanges();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelProgressDialog();

        if (dialogBox != null) {
            dialogBox.dismiss();
        }
    }

    class BottomSheetViews {
        @OnClick(R.id.ll_change_profile_image_dialog_row)
        public void onChangeProfileImageClicked() {
            mPresenter.handleProfileImageChangeRequest();
            bottomSheetDialog.dismiss();
        }

        @OnClick(R.id.ll_remove_profile_image_dialog_row)
        public void onRemoveProfileImageClicked() {
            mEditProfilePresenter.handleProfileImageRemoved();
            bottomSheetDialog.dismiss();
        }
    }
}
