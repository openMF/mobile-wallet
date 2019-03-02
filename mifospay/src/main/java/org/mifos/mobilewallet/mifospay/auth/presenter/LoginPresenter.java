package org.mifos.mobilewallet.mifospay.auth.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;
import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.core.domain.model.user.User;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser;
import org.mifos.mobilewallet.core.domain.usecase.user.FetchUserDetails;
import org.mifos.mobilewallet.mifospay.auth.AuthContract;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;

import javax.inject.Inject;

/**
 * Created by naman on 16/6/17.
 */

public class LoginPresenter implements AuthContract.LoginPresenter {

    private final UseCaseHandler mUsecaseHandler;
    private final PreferencesHelper preferencesHelper;
    @Inject
    AuthenticateUser authenticateUserUseCase;
    @Inject
    FetchClientData fetchClientDataUseCase;
    @Inject
    FetchUserDetails fetchUserDetailsUseCase;
    private AuthContract.LoginView mLoginView;

    @Inject
    public LoginPresenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        this.mUsecaseHandler = useCaseHandler;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mLoginView = (AuthContract.LoginView) baseView;
        mLoginView.setPresenter(this);
    }

    @Override
    public void handleLoginButtonStatus(String usernameContent, String passwordContent) {
        if (isStringEmpty(usernameContent) || isStringEmpty(passwordContent)) {
            mLoginView.disableLoginButton();
        } else {
            mLoginView.enableLoginButton();
        }
    }

    private boolean isStringEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public void loginUser(String username, String password) {

        authenticateUserUseCase.setRequestValues(new
                AuthenticateUser.RequestValues(username, password));
        final AuthenticateUser.RequestValues requestValue =
                authenticateUserUseCase.getRequestValues();

        mUsecaseHandler.execute(authenticateUserUseCase, requestValue,
                new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(AuthenticateUser.ResponseValue response) {
                        createAuthenticatedService(response.getUser());
                        fetchClientData();
                        fetchUserDetails(response.getUser());
                    }

                    @Override
                    public void onError(String message) {
                        mLoginView.loginFail(message);
                    }
                });


    }

    private void fetchUserDetails(final User user) {
        mUsecaseHandler.execute(fetchUserDetailsUseCase,
                new FetchUserDetails.RequestValues(user.getUserId()),
                new UseCase.UseCaseCallback<FetchUserDetails.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchUserDetails.ResponseValue response) {
                        saveUserDetails(user, response.getUserWithRole());
                    }

                    @Override
                    public void onError(String message) {
                        DebugUtil.log(message);
                    }
                });
    }

    private void fetchClientData() {
        mUsecaseHandler.execute(fetchClientDataUseCase, null,
                new UseCase.UseCaseCallback<FetchClientData.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchClientData.ResponseValue response) {
                        saveClientDetails(response.getUserDetails());

                        if (!response.getUserDetails().getName().equals("")) {
                            mLoginView.loginSuccess();
                        }
                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }

    private void createAuthenticatedService(User user) {

        final String authToken = Constants.BASIC +
                user.getAuthenticationKey();

        preferencesHelper.saveToken(authToken);
        FineractApiManager.createSelfService(preferencesHelper.getToken());

    }

    private void saveUserDetails(User user,
            UserWithRole userWithRole) {
        final String userName = user.getUserName();
        final long userID = user.getUserId();

        preferencesHelper.saveUsername(userName);
        preferencesHelper.setUserId(userID);
        preferencesHelper.saveEmail(userWithRole.getEmail());
    }

    private void saveClientDetails(Client client) {
        preferencesHelper.saveFullName(client.getName());
        preferencesHelper.setClientId(client.getClientId());
        preferencesHelper.saveMobile(client.getMobileNo());
    }
}
