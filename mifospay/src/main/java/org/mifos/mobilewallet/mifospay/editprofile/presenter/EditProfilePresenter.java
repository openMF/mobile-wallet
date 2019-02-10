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
 * This class is the Presenter component of the edit profile package.
 * @author ankur
 * @since 27/June/2018
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

    /**
     * Constructor for class EditProfilePresenter which is used to initialize
     * the objects that are passed as arguments.
     * @param useCaseHandler : An object of UseCaseHandler
     * @param preferencesHelper : An object of PreferencesHelper
     */
    @Inject
    public EditProfilePresenter(UseCaseHandler useCaseHandler,
            PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        mPreferencesHelper = preferencesHelper;
    }

    /**
     * Attaches View to the Presenter.
     */
    @Override
    public void attachView(BaseView baseView) {
        mEditProfileView = (EditProfileContract.EditProfileView) baseView;
        mEditProfileView.setPresenter(this);
    }

    /**
     * An overridden method from Contract to update password.
     * @param currentPassword : currentPassword for conformation
     * @param newPassword : newPassword to update
     */
    @Override
    public void updatePassword(String currentPassword, final String newPassword) {
        /**
         * Authenticate and then update
         */
        mUseCaseHandler.execute(authenticateUserUseCase,
                new AuthenticateUser.RequestValues(mPreferencesHelper.getUsername(),
                        currentPassword),
                new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(AuthenticateUser.ResponseValue response) {

                        mUseCaseHandler.execute(updateUserUseCase,
                                new UpdateUser.RequestValues(
                                        new UpdateUserEntityPassword(newPassword),
                                        (int) mPreferencesHelper.getUserId()),
                                new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
                                    /**
                                     * An overridden method called when the task
                                     * completes successfully.
                                     * @param response : The result of the Task
                                     */
                                    @Override
                                    public void onSuccess(UpdateUser.ResponseValue response) {
                                        mEditProfileView.onUpdatePasswordSuccess();
                                    }

                                    /**
                                     * An overridden method called when the task
                                     * fails with an exception.
                                     * @param message : The exception that caused the task to fail
                                     */
                                    @Override
                                    public void onError(String message) {
                                        mEditProfileView.onUpdatePasswordError(message);
                                    }
                                });
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdatePasswordError("Wrong password");
                    }
                });
    }

    /**
     * An overridden method from Contract to update passcode.
     * This feature is not available in Mifos PassCode library.
     */
    @Override
    public void updatePasscode(String currentPasscode, String newPasscode) {
    }

    /**
     * An overridden method from Contract to update email.
     * @param email : New email to update
     */
    @Override
    public void updateEmail(final String email) {
        mUseCaseHandler.execute(updateUserUseCase,
                new UpdateUser.RequestValues(new UpdateUserEntityEmail(email),
                        (int) mPreferencesHelper.getUserId()),
                new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(UpdateUser.ResponseValue response) {
                        mPreferencesHelper.saveEmail(email);
                        mEditProfileView.onUpdateEmailSuccess(email);
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdateEmailError(message);
                    }
                });
    }

    /**
     * An overridden method from Contract to update mobile number.
     * @param fullNumber : Mobile number to be update
     */
    @Override
    public void updateMobile(final String fullNumber) {
        mUseCaseHandler.execute(updateClientUseCase,
                new UpdateClient.RequestValues(new UpdateClientEntityMobile(fullNumber),
                        (int) mPreferencesHelper.getClientId()),
                new UseCase.UseCaseCallback<UpdateClient.ResponseValue>() {
                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(UpdateClient.ResponseValue response) {
                        mPreferencesHelper.saveMobile(fullNumber);
                        mEditProfileView.onUpdateMobileSuccess(fullNumber);
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {
                        mEditProfileView.onUpdateMobileError(message);
                    }
                });
    }

    /**
     * An overridden method from Contract to handle profile image change request.
     */
    @Override
    public void handleProfileImageChangeRequest() {
        mEditProfileView.changeProfileImage();
    }

    /**
     * An overridden method from Contract to handle profile image remove request.
     */
    @Override
    public void handleProfileImageRemoved() {
        mEditProfileView.removeProfileImage();
        setDefaultUserImage();
    }

    /**
     * An overridden method from Contract to fetch User details like email and mobile number.
     */
    @Override
    public void fetchUserDetails() {
        mEditProfileView.setEmail(mPreferencesHelper.getEmail());
        mEditProfileView.setMobile(mPreferencesHelper.getMobile());
        setDefaultUserImage();
    }

    /**
     * An overridden method from Contract to set profile image to default user image.
     */
    private void setDefaultUserImage() {
        mEditProfileView.setImage(mPreferencesHelper.getFullName());
    }
}
