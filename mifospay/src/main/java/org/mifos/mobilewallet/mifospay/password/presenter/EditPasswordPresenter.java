package org.mifos.mobilewallet.mifospay.password.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.model.user.UpdateUserEntityPassword;
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser;
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.password.EditPasswordContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;

import javax.inject.Inject;

public class EditPasswordPresenter implements EditPasswordContract.EditPasswordPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    UpdateUser updateUserUseCase;

    @Inject
    AuthenticateUser authenticateUserUseCase;

    private EditPasswordContract.EditPasswordView mEditPasswordView;

    @Inject
    public EditPasswordPresenter(UseCaseHandler useCaseHandler,
            PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mEditPasswordView = (EditPasswordContract.EditPasswordView) baseView;
        mEditPasswordView.setPresenter(this);
    }

    @Override
    public void handleSavePasswordButtonStatus(String currentPassword,
                                               String newPassword,
                                               String newPasswordRepeat) {
        if (currentPassword.equals("") || newPassword.equals("") ||
                newPasswordRepeat.equals("")) {
            mEditPasswordView.disableSavePasswordButton();
        } else {
            if (newPassword.equals(newPasswordRepeat)) {
                mEditPasswordView.enableSavePasswordButton();
            } else {
                mEditPasswordView.disableSavePasswordButton();
            }
        }
    }

    @Override
    public void updatePassword(String currentPassword, final String newPassword,
            final String newPasswordRepeat) {
        mEditPasswordView.startProgressBar();
        if (isNotEmpty(currentPassword) && isNotEmpty(newPassword)
                && isNotEmpty(newPasswordRepeat)) {
            if (currentPassword.equals(newPassword)) {
                mEditPasswordView.stopProgressBar();
                mEditPasswordView.showError(Constants.ERROR_PASSWORDS_CANT_BE_SAME);
            } else if (isNewPasswordValid(newPassword, newPasswordRepeat)) {
                updatePassword(currentPassword, newPassword);
            } else {
                mEditPasswordView.stopProgressBar();
                mEditPasswordView.showError(Constants.ERROR_VALIDATING_PASSWORD);
            }
        } else {
            mEditPasswordView.stopProgressBar();
            mEditPasswordView.showError(Constants.ERROR_FIELDS_CANNOT_BE_EMPTY);
        }
    }

    private boolean isNotEmpty(String str) {
        return !(str == null || str.isEmpty());
    }

    private boolean isNewPasswordValid(String newPassword, String newPasswordRepeat) {
        return newPassword.equals(newPasswordRepeat);
    }

    private void updatePassword(String currentPassword, final String newPassword) {
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
                                        mEditPasswordView.stopProgressBar();
                                        mEditPasswordView.closeActivity();
                                    }

                                    @Override
                                    public void onError(String message) {
                                        mEditPasswordView.stopProgressBar();
                                        mEditPasswordView.showError(message);
                                    }
                                });
                    }

                    @Override
                    public void onError(String message) {
                        mEditPasswordView.stopProgressBar();
                        mEditPasswordView.showError("Wrong password");
                    }
                });
    }
}
