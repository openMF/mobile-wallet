package org.mifos.mobilewallet.auth.presenter;

import org.mifos.mobilewallet.auth.AuthContract;
import org.mifos.mobilewallet.base.BaseView;
import org.mifos.mobilewallet.data.local.PreferencesHelper;

import javax.inject.Inject;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.core.domain.model.Client;
import org.mifos.mobilewallet.core.domain.model.User;
import org.mifos.mobilewallet.core.domain.usecase.AuthenticateUser;
import org.mifos.mobilewallet.core.domain.usecase.FetchClientData;
import org.mifos.mobilewallet.core.utils.Constants;

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
    FetchClientData fetchClientDataUseCase;

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
        mUsecaseHandler.execute(fetchClientDataUseCase ,
                null,
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

    private void saveUserDetails(User user) {
        final String userName = user.getUserName();
        final long userID = user.getUserId();

        preferencesHelper.saveUsername(userName);
        preferencesHelper.setUserId(userID);

    }

    private void saveClientDetails(Client client) {
        preferencesHelper.saveFullName(client.getName());
        preferencesHelper.setClientId(client.getClientId());
    }
}
