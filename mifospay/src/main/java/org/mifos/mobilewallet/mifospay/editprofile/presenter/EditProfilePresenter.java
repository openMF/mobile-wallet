package org.mifos.mobilewallet.mifospay.editprofile.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.client.UpdateClientEntityMobile;
import org.mifos.mobilewallet.core.domain.model.user.UpdateUserEntityEmail;
import org.mifos.mobilewallet.core.domain.usecase.client.UpdateClient;
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser;
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.editprofile.EditProfileContract;

import javax.inject.Inject;

/**
 * Created by ankur on 27/June/2018
 */

public class EditProfilePresenter implements EditProfileContract.EditProfilePresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final PreferencesHelper mPreferencesHelper;
    @Inject
    UpdateUser updateUserUseCase;
    @Inject
    UpdateClient updateClientUseCase;
    @Inject
    AuthenticateUser authenticateUserUseCase;
    private EditProfileContract.EditProfileView mEditProfileView;

    @Inject
    public EditProfilePresenter(UseCaseHandler useCaseHandler,
            PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mEditProfileView = (EditProfileContract.EditProfileView) baseView;
        mEditProfileView.setPresenter(this);
    }

    @Override
    public void fetchUserDetails() {
        showUserImageOrDefault();
        showUsernameIfNotEmpty();
        showEmailIfNotEmpty();
        showVpaIfNotEmpty();
        showMobielIfNotEmpty();
    }

    private void showUserImageOrDefault() {
        /*
            TODO:

            We could check if user has a custom image and then fetch it from the db here
            and show the custom image on success or default on error/if user doesn't have one

         */
        mEditProfileView.showDefaultImageByUsername(mPreferencesHelper.getFullName());
    }

    private void showUsernameIfNotEmpty() {
        if (!mPreferencesHelper.getUsername().isEmpty()) {
            mEditProfileView.showUsername(mPreferencesHelper.getUsername());
        }
    }

    private void showEmailIfNotEmpty() {
        if (!mPreferencesHelper.getEmail().isEmpty()) {
            mEditProfileView.showEmail(mPreferencesHelper.getEmail());
        }
    }

    private void showVpaIfNotEmpty() {
        if (!mPreferencesHelper.getClientVpa().isEmpty()) {
            mEditProfileView.showVpa(mPreferencesHelper.getClientVpa());
        }
    }

    private void showMobielIfNotEmpty() {
        if (!mPreferencesHelper.getMobile().isEmpty()) {
            mEditProfileView.showMobileNumber(mPreferencesHelper.getMobile());
        }
    }

    @Override
    public void updateInputById(int id, String content) {
        switch (id) {
            case R.id.et_edit_profile_username:
                break;
            case R.id.et_edit_profile_email:
                updateEmail(content);
                break;
            case R.id.et_edit_profile_vpa:
                break;
            case R.id.et_edit_profile_mobile:
                updateMobile(content);
                break;
        }
    }

    @Override
    public void updateEmail(final String email) {
        mEditProfileView.startProgressBar();
        mUseCaseHandler.execute(updateUserUseCase,
                new UpdateUser.RequestValues(new UpdateUserEntityEmail(email),
                        (int) mPreferencesHelper.getUserId()),
                new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateUser.ResponseValue response) {
                        mPreferencesHelper.saveEmail(email);
                        showEmailIfNotEmpty();
                        mEditProfileView.stopProgressBar();
                    }

                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdateEmailError(message);
                        mEditProfileView.showFab();
                        mEditProfileView.stopProgressBar();
                    }
                });
    }

    @Override
    public void updateMobile(final String fullNumber) {
        mEditProfileView.startProgressBar();
        mUseCaseHandler.execute(updateClientUseCase,
                new UpdateClient.RequestValues(new UpdateClientEntityMobile(fullNumber),
                        (int) mPreferencesHelper.getClientId()),
                new UseCase.UseCaseCallback<UpdateClient.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateClient.ResponseValue response) {
                        mPreferencesHelper.saveMobile(fullNumber);
                        showMobielIfNotEmpty();
                        mEditProfileView.stopProgressBar();
                    }

                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdateMobileError(message);
                        mEditProfileView.showFab();
                        mEditProfileView.stopProgressBar();
                    }
                });
    }

    @Override
    public void handleProfileImageChangeRequest() {
        mEditProfileView.changeProfileImage();
    }

    @Override
    public void handleProfileImageRemoved() {
        mEditProfileView.removeProfileImage();
        mEditProfileView.showDefaultImageByUsername(mPreferencesHelper.getFullName());
    }

    @Override
    public void handleClickProfileImageRequest() {
        mEditProfileView.clickProfileImage();
    }

    @Override
    public void handleNecessaryDataSave() {
        mEditProfileView.showFab();
    }

    @Override
    public void handleExitOnUnsavedChanges() {
        mEditProfileView.hideFab();
        mEditProfileView.showDiscardChangesDialog();
    }

    @Override
    public void onDialogNegative() {
        mEditProfileView.showFab();
    }

    @Override
    public void onDialogPositive() {
        mEditProfileView.closeActivity();
    }
}
