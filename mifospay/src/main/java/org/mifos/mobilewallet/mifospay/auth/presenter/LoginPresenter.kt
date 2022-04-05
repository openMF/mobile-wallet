package org.mifos.mobilewallet.mifospay.auth.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole
import org.mifos.mobilewallet.core.domain.model.client.Client
import org.mifos.mobilewallet.core.domain.model.user.User
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser
import org.mifos.mobilewallet.core.domain.usecase.user.FetchUserDetails
import org.mifos.mobilewallet.mifospay.auth.AuthContract
import org.mifos.mobilewallet.mifospay.auth.AuthContract.LoginView
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import javax.inject.Inject

/**
 * Created by naman on 16/6/17.
 */
class LoginPresenter @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val preferencesHelper: PreferencesHelper
) : AuthContract.LoginPresenter {
    @JvmField
    @Inject
    var authenticateUserUseCase: AuthenticateUser? = null

    @JvmField
    @Inject
    var fetchClientDataUseCase: FetchClientData? = null

    @JvmField
    @Inject
    var fetchUserDetailsUseCase: FetchUserDetails? = null
    private var mLoginView: LoginView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mLoginView = baseView as LoginView?
        mLoginView!!.setPresenter(this)
    }

    override fun handleLoginButtonStatus(usernameContent: String?, passwordContent: String?) {
        if (isStringEmpty(usernameContent) || isStringEmpty(passwordContent)) {
            mLoginView!!.disableLoginButton()
        } else {
            mLoginView!!.enableLoginButton()
        }
    }

    private fun isStringEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    override fun loginUser(username: String?, password: String?) {
        authenticateUserUseCase!!.requestValues = AuthenticateUser.RequestValues(username, password)
        val requestValue = authenticateUserUseCase!!.requestValues
        mUsecaseHandler.execute(authenticateUserUseCase, requestValue,
            object : UseCaseCallback<AuthenticateUser.ResponseValue?> {
                override fun onSuccess(response: AuthenticateUser.ResponseValue?) {
                    if (response != null) {
                        createAuthenticatedService(response.user)
                    }
                    fetchClientData()
                    response?.user?.let { fetchUserDetails(it) }
                }

                override fun onError(message: String) {
                    mLoginView!!.loginFail(message)
                }
            })
    }

    private fun fetchUserDetails(user: User) {
        mUsecaseHandler.execute(fetchUserDetailsUseCase,
            FetchUserDetails.RequestValues(user.userId),
            object : UseCaseCallback<FetchUserDetails.ResponseValue?> {
                override fun onSuccess(response: FetchUserDetails.ResponseValue?) {
                    response?.let { saveUserDetails(user, it.userWithRole) }
                }

                override fun onError(message: String) {
                    DebugUtil.log(message)
                }
            })
    }

    private fun fetchClientData() {
        mUsecaseHandler.execute(fetchClientDataUseCase, null,
            object : UseCaseCallback<FetchClientData.ResponseValue?> {
                override fun onSuccess(response: FetchClientData.ResponseValue?) {
                    if (response != null) {
                        saveClientDetails(response.userDetails)
                    }
                    if (response != null) {
                        if (response.userDetails.name != "") {
                            mLoginView!!.loginSuccess()
                        }
                    }
                }

                override fun onError(message: String) {}
            })
    }

    private fun createAuthenticatedService(user: User) {
        val authToken = Constants.BASIC +
                user.authenticationKey
        preferencesHelper.saveToken(authToken)
        FineractApiManager.createSelfService(preferencesHelper.token)
    }

    private fun saveUserDetails(
        user: User,
        userWithRole: UserWithRole
    ) {
        val userName = user.userName
        val userID = user.userId
        preferencesHelper.saveUsername(userName)
        preferencesHelper.userId = userID
        preferencesHelper.saveEmail(userWithRole.email)
    }

    private fun saveClientDetails(client: Client) {
        preferencesHelper.saveFullName(client.name)
        preferencesHelper.clientId = client.clientId
        preferencesHelper.saveMobile(client.mobileNo)
    }
}