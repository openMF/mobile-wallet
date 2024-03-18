package org.mifos.mobilewallet.mifospay.auth

import androidx.lifecycle.ViewModel
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import com.mifos.mobilewallet.model.domain.user.User
import com.mifos.mobilewallet.model.entity.UserWithRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser
import org.mifos.mobilewallet.core.domain.usecase.user.FetchUserDetails
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import javax.inject.Inject

/**
 * Created by naman on 16/6/17.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val authenticateUserUseCase: AuthenticateUser,
    private val fetchClientDataUseCase: FetchClientData,
    private val fetchUserDetailsUseCase: FetchUserDetails,
    private val preferencesHelper: PreferencesHelper,
    private val passcodePreferencesHelper: PasscodePreferencesHelper,
) : ViewModel() {

    private val _showProgress = MutableStateFlow(false)
    val showProgress: StateFlow<Boolean> = _showProgress

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess

    val isPassCodeExist = passcodePreferencesHelper.passCode.isNotEmpty()

    fun updateProgressState(isVisible: Boolean) {
        _showProgress.update { isVisible }
    }

    fun updateIsLoginSuccess(isLoginSuccess: Boolean) {
        _isLoginSuccess.update { isLoginSuccess }
    }


    fun loginUser(
        username: String?,
        password: String?,
        onLoginFailed: (String) -> Unit
    ) {
        updateProgressState(true)
        authenticateUserUseCase.walletRequestValues =
            AuthenticateUser.RequestValues(username, password)

        val requestValue = authenticateUserUseCase.walletRequestValues
        mUsecaseHandler.execute(authenticateUserUseCase, requestValue,
            object : UseCaseCallback<AuthenticateUser.ResponseValue> {
                override fun onSuccess(response: AuthenticateUser.ResponseValue) {
                    createAuthenticatedService(response.user)
                    fetchClientData()
                    fetchUserDetails(response.user)
                }

                override fun onError(message: String) {
                    updateProgressState(false)
                    onLoginFailed(message)
                }
            })
    }

    private fun fetchUserDetails(user: User) {
        mUsecaseHandler.execute(fetchUserDetailsUseCase,
            FetchUserDetails.RequestValues(user.userId),
            object : UseCaseCallback<FetchUserDetails.ResponseValue> {
                override fun onSuccess(response: FetchUserDetails.ResponseValue) {
                    saveUserDetails(user, response.userWithRole)
                }

                override fun onError(message: String) {
                    updateProgressState(false)
                    DebugUtil.log(message)
                }
            })
    }

    private fun fetchClientData() {
        mUsecaseHandler.execute(fetchClientDataUseCase, FetchClientData.RequestValues(0),
            object : UseCaseCallback<FetchClientData.ResponseValue> {
                override fun onSuccess(response: FetchClientData.ResponseValue) {
                    saveClientDetails(response.userDetails)
                    updateProgressState(false)
                    if (response.userDetails.name != "") {
                       updateIsLoginSuccess(true)
                    }
                }

                override fun onError(message: String) {
                    updateProgressState(false)
                }
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