package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.data.local.PreferencesHelper;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.data.fineract.api.FineractApiManager;
import mifos.org.mobilewallet.core.domain.model.ClientDetails;
import mifos.org.mobilewallet.core.domain.model.User;
import mifos.org.mobilewallet.core.domain.usecase.AuthenticateUser;
import mifos.org.mobilewallet.core.domain.usecase.FetchClientData;
import mifos.org.mobilewallet.core.utils.Constants;

/**
 * Created by naman on 16/6/17.
 */

public class LoginPresenter implements AuthContract.LoginPresenter {

    private AuthContract.LoginView mLoginView;
    private final UseCaseHandler mUsecaseHandler;

    private final PreferencesHelper preferencesHelper;

    @Inject
    AuthenticateUser authenticateUser;

    @Inject
    FetchClientData fetchClientData;

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


    public void loginUser(String username, String password) {

        authenticateUser.setRequestValues(new AuthenticateUser.RequestValues(username, password));
        final AuthenticateUser.RequestValues requestValue = authenticateUser.getRequestValues();

        mUsecaseHandler.execute(authenticateUser, requestValue,
                new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(AuthenticateUser.ResponseValue response) {
                        saveUserDetails(response.getUser());
                        createAuthenticatedService(response.getUser());
                        fetchClientData();
                    }

                    @Override
                    public void onError(String message) {
                        mLoginView.loginFail(message);
                    }
                });


    }

    private void fetchClientData() {
        mUsecaseHandler.execute(fetchClientData ,
                new FetchClientData.RequestValues(preferencesHelper.getClientId()),
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
        FineractApiManager.createService(preferencesHelper.getToken());

    }

    private void saveUserDetails(User user) {
        final String userName = user.getUserName();
        final long userID = user.getUserId();

        preferencesHelper.saveUsername(userName);
        preferencesHelper.setUserId(userID);

    }

    private void saveClientDetails(ClientDetails clientDetails) {
        preferencesHelper.saveFullName(clientDetails.getName());
        preferencesHelper.setClientId(clientDetails.getClientId());
    }
}
