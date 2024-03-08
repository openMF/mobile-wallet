package org.mifos.mobilewallet.mifospay.auth.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import com.mifos.mobilewallet.model.entity.UserWithRole
import com.mifos.mobilewallet.model.domain.user.User
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser
import org.mifos.mobilewallet.core.domain.usecase.user.FetchUserDetails
import org.mifos.mobilewallet.mifospay.auth.AuthContract
import org.mifos.mobilewallet.mifospay.auth.AuthContract.LoginView
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.datastore.PreferencesHelper
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

    @Inject
    lateinit var authenticateUserUseCase: AuthenticateUser

    @Inject
    lateinit var fetchClientDataUseCase: FetchClientData

    @Inject
    lateinit var fetchUserDetailsUseCase: FetchUserDetails
    private lateinit var mLoginView: LoginView

    override fun loginUser(username: String?, password: String?) {
        authenticateUserUseCase.walletRequestValues = AuthenticateUser.RequestValues(username, password)

        val requestValue = authenticateUserUseCase.walletRequestValues
        mUsecaseHandler.execute(authenticateUserUseCase, requestValue,
            object : UseCaseCallback<AuthenticateUser.ResponseValue> {
                override fun onSuccess(response: AuthenticateUser.ResponseValue) {
                    createAuthenticatedService(response.user)
                    fetchClientData()
                    fetchUserDetails(response.user)
                }

                override fun onError(message: String) {
                    mLoginView.loginFail(message)
                }
            })
    }

    override fun attachView(baseView: BaseView<*>?) {
        mLoginView = baseView as LoginView
        mLoginView.setPresenter(this)
    }

    private fun fetchUserDetails(user: User) {
        mUsecaseHandler.execute(fetchUserDetailsUseCase,
            FetchUserDetails.RequestValues(user.userId),
            object : UseCaseCallback<FetchUserDetails.ResponseValue> {
                override fun onSuccess(response: FetchUserDetails.ResponseValue) {
                    saveUserDetails(user, response.userWithRole)
                }

                override fun onError(message: String) {
                    DebugUtil.log(message)
                }
            })
    }

    private fun fetchClientData() {
        mUsecaseHandler.execute(fetchClientDataUseCase, FetchClientData.RequestValues(0),
            object : UseCaseCallback<FetchClientData.ResponseValue> {
                override fun onSuccess(response: FetchClientData.ResponseValue) {
                    saveClientDetails(response.userDetails)
                    if (response.userDetails.name != "") {
                        mLoginView.loginSuccess()
                    }
                }

                override fun onError(message: String) {}
            })
    }

    private fun createAuthenticatedService(user: User) {
        val authToken = Constants.BASIC + user.authenticationKey
        preferencesHelper.saveToken(authToken)
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

    private fun saveClientDetails(client: com.mifos.mobilewallet.model.domain.client.Client?) {
        preferencesHelper.saveFullName(client?.name)
        preferencesHelper.clientId = client?.clientId!!
        preferencesHelper.saveMobile(client.mobileNo)
    }
}