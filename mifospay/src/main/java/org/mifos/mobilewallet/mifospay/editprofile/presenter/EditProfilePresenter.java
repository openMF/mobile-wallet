package org.mifos.mobilewallet.mifospay.editprofile.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.client.UpdateClientEntityMobile;
import org.mifos.mobilewallet.core.domain.model.user.UpdateUserEntityEmail;
import org.mifos.mobilewallet.core.domain.model.user.UpdateUserEntityPassword;
import org.mifos.mobilewallet.core.domain.usecase.client.UpdateClient;
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser;
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser;
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
    public void updatePassword(String currentPassword, final String newPassword) {
        // authenticate and then update
        mUseCaseHandler.execute(authenticateUserUseCase,
                new AuthenticateUser.RequestValues(mPreferencesHelper.getUsername(),
                        currentPassword),
                new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(AuthenticateUser.ResponseValue response) {

                        mUseCaseHandler.execute(updateUserUseCase,
                                new UpdateUser.RequestValues(
                                        new UpdateUserEntityPassword(newPassword),
                                        (int) mPreferencesHelper.getUserId()),
                                new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
                                    @Override
                                    public void onSuccess(UpdateUser.ResponseValue response) {
                                        mEditProfileView.onUpdatePasswordSuccess();
                                    }

                                    @Override
                                    public void onError(String message) {
                                        mEditProfileView.onUpdatePasswordError(message);
                                    }
                                });
                    }

                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdatePasswordError("Wrong password");
                    }
                });
    }

    @Override
    public void updatePasscode(String currentPasscode, String newPasscode) {
        // feature not available in MifosPassCode library
    }

    @Override
    public void updateEmail(final String email) {
        mUseCaseHandler.execute(updateUserUseCase,
                new UpdateUser.RequestValues(new UpdateUserEntityEmail(email),
                        (int) mPreferencesHelper.getUserId()),
                new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateUser.ResponseValue response) {
                        mPreferencesHelper.saveEmail(email);
                        mEditProfileView.onUpdateEmailSuccess(email);
                    }

                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdateEmailError(message);
                    }
                });
    }

    @Override
    public void updateMobile(final String fullNumber) {
        mUseCaseHandler.execute(updateClientUseCase,
                new UpdateClient.RequestValues(new UpdateClientEntityMobile(fullNumber),
                        (int) mPreferencesHelper.getClientId()),
                new UseCase.UseCaseCallback<UpdateClient.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateClient.ResponseValue response) {
                        mPreferencesHelper.saveMobile(fullNumber);
                        mEditProfileView.onUpdateMobileSuccess(fullNumber);
                    }

                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdateMobileError(message);
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
        setDefaultUserImage();
    }

    @Override
    public void fetchUserDetails() {
        mEditProfileView.setEmail(mPreferencesHelper.getEmail());
        mEditProfileView.setMobile(mPreferencesHelper.getMobile());
        setDefaultUserImage();
    }

    private void setDefaultUserImage() {
        mEditProfileView.setImage(mPreferencesHelper.getFullName());
    }
}
