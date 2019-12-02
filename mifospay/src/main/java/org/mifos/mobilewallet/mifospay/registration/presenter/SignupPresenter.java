package org.mifos.mobilewallet.mifospay.registration.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;
import org.mifos.mobilewallet.core.domain.model.client.Client;
import org.mifos.mobilewallet.core.domain.model.client.NewClient;
import org.mifos.mobilewallet.core.domain.model.user.NewUser;
import org.mifos.mobilewallet.core.domain.model.user.UpdateUserEntityClients;
import org.mifos.mobilewallet.core.domain.model.user.User;
import org.mifos.mobilewallet.core.domain.usecase.client.CreateClient;
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser;
import org.mifos.mobilewallet.core.domain.usecase.user.CreateUser;
import org.mifos.mobilewallet.core.domain.usecase.user.DeleteUser;
import org.mifos.mobilewallet.core.domain.usecase.user.FetchUserDetails;
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper;
import org.mifos.mobilewallet.mifospay.registration.RegistrationContract;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.DebugUtil;
import org.mifos.mobilewallet.mifospay.utils.PasswordStrength;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by ankur on 21/June/2018
 */

public class SignupPresenter implements RegistrationContract.SignupPresenter {

    private final UseCaseHandler mUseCaseHandler;
    private final PreferencesHelper mPreferencesHelper;
    RegistrationContract.SignupView mSignupView;
    @Inject
    SearchClient searchClientUseCase;
    @Inject
    CreateClient createClientUseCase;
    @Inject
    CreateUser createUserUseCase;
    @Inject
    UpdateUser updateUserUseCase;
    @Inject
    AuthenticateUser authenticateUserUseCase;
    @Inject
    FetchClientData fetchClientDataUseCase;
    @Inject
    DeleteUser deleteUserUseCase;
    @Inject
    FetchUserDetails fetchUserDetailsUseCase;

    private String firstName, lastName, mobileNumber, email, businessName, addressLine1,
            addressLine2, pincode, city, countryName, username, password, stateId, countryId;

    private int mifosSavingsProductId;

    @Inject
    public SignupPresenter(UseCaseHandler useCaseHandler, PreferencesHelper preferencesHelper) {
        mUseCaseHandler = useCaseHandler;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSignupView = (RegistrationContract.SignupView) baseView;
        mSignupView.setPresenter(this);
    }

    @Override
    public void checkPasswordStrength(String password) {
        PasswordStrength p = new PasswordStrength(password);
        mSignupView.updatePasswordStrength(p.getStrengthStringId(),
                p.getColorResId(), p.getValue());
    }

    @Override
    public void registerUser(final String firstName, final String lastName,
            final String mobileNumber, final String email, final String businessName,
            final String addressline1, final String addressline2, final String pincode,
            final String city, String countryName, final String username, final String password,
            final String stateId, final String countryId, int mifosSavingProductId) {

        // 0. Unique Mobile Number (checked in MOBILE VERIFICATION ACTIVITY)
        // 1. Check for unique external id and username
        // 2. Create user
        // 3. Create Client
        // 4. Update User and connect client with user

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.businessName = businessName;
        this.addressLine1 = addressline1;
        this.addressLine2 = addressline2;
        this.pincode = pincode;
        this.city = city;
        this.countryName = countryName;
        this.username = username;
        this.password = password;
        this.stateId = stateId;
        this.countryId = countryId;
        this.mobileNumber = mobileNumber;
        this.mifosSavingsProductId = mifosSavingProductId;

        mUseCaseHandler.execute(searchClientUseCase,
                new SearchClient.RequestValues(username + "@mifos"),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        mSignupView.onRegisterFailed("Username already exists.");
                    }

                    @Override
                    public void onError(String message) {
                        createUser();
                    }
                });
    }

    private void createUser() {

        NewUser newUser = new NewUser(username, firstName, lastName, email, password);

        mUseCaseHandler.execute(createUserUseCase, new CreateUser.RequestValues(newUser),
                new UseCase.UseCaseCallback<CreateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(CreateUser.ResponseValue response) {
                        createClient(response.getUserId());
                    }

                    @Override
                    public void onError(String message) {
                        DebugUtil.log(message);
                        mSignupView.onRegisterFailed(message);
                    }
                });
    }

    private void createClient(final int userId) {

        DebugUtil.log("mob::::: ", mobileNumber);
        NewClient newClient = new NewClient(businessName, username, addressLine1,
                addressLine2, city, pincode, stateId, countryId, mobileNumber,
                mifosSavingsProductId);

        mUseCaseHandler.execute(createClientUseCase,
                new CreateClient.RequestValues(newClient),
                new UseCase.UseCaseCallback<CreateClient.ResponseValue>() {
                    @Override
                    public void onSuccess(CreateClient.ResponseValue response) {
                        DebugUtil.log(response.getClientId());
                        ArrayList<Integer> clients = new ArrayList<>();
                        clients.add(response.getClientId());
                        updateClient(clients, userId);
                    }

                    @Override
                    public void onError(String message) {
                        // delete user
                        DebugUtil.log(message);
                        mSignupView.onRegisterFailed(message);
                        deleteUser(userId);
                    }
                });
    }

    private void updateClient(ArrayList<Integer> clients, int userId) {
        mUseCaseHandler.execute(updateUserUseCase,
                new UpdateUser.RequestValues(new UpdateUserEntityClients(clients), userId),
                new UseCase.UseCaseCallback<UpdateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(UpdateUser.ResponseValue response) {
                        loginUser(username, password);
                    }

                    @Override
                    public void onError(String message) {
                        // connect client later
                        DebugUtil.log(message);
                        mSignupView.onRegisterSuccess("update client error");
                    }
                });
    }

    private void loginUser(String username, String password) {
        authenticateUserUseCase.setRequestValues(new
                AuthenticateUser.RequestValues(username, password));
        final AuthenticateUser.RequestValues requestValue =
                authenticateUserUseCase.getRequestValues();

        mUseCaseHandler.execute(authenticateUserUseCase, requestValue,
                new UseCase.UseCaseCallback<AuthenticateUser.ResponseValue>() {
                    @Override
                    public void onSuccess(AuthenticateUser.ResponseValue response) {
                        createAuthenticatedService(response.getUser());
                        fetchClientData();
                        fetchUserDetails(response.getUser());
                    }

                    @Override
                    public void onError(String message) {
                        mSignupView.onRegisterSuccess("Login Failed");
                    }
                });


    }

    private void fetchUserDetails(final User user) {
        mUseCaseHandler.execute(fetchUserDetailsUseCase,
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
        mUseCaseHandler.execute(fetchClientDataUseCase, null,
                new UseCase.UseCaseCallback<FetchClientData.ResponseValue>() {
                    @Override
                    public void onSuccess(FetchClientData.ResponseValue response) {
                        saveClientDetails(response.getUserDetails());

                        if (!response.getUserDetails().getName().equals("")) {
                            mSignupView.loginSuccess();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        mSignupView.onRegisterSuccess("Fetch Client Error");
                    }
                });
    }

    private void createAuthenticatedService(User user) {

        final String authToken = Constants.BASIC +
                user.getAuthenticationKey();

        mPreferencesHelper.saveToken(authToken);
        FineractApiManager.createSelfService(mPreferencesHelper.getToken());
    }

    private void saveUserDetails(User user,
            UserWithRole userWithRole) {
        final String userName = user.getUserName();
        final long userID = user.getUserId();

        mPreferencesHelper.saveUsername(userName);
        mPreferencesHelper.setUserId(userID);
        mPreferencesHelper.saveEmail(userWithRole.getEmail());
    }

    private void saveClientDetails(Client client) {
        mPreferencesHelper.saveFullName(client.getName());
        mPreferencesHelper.setClientId(client.getClientId());
        mPreferencesHelper.saveMobile(client.getMobileNo());
    }

    private void deleteUser(int userId) {
        mUseCaseHandler.execute(deleteUserUseCase, new DeleteUser.RequestValues(userId),
                new UseCase.UseCaseCallback<DeleteUser.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteUser.ResponseValue response) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
