package org.mifos.mobilewallet.mifospay.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.UserWithRole
import org.mifos.mobilewallet.datastore.PreferencesHelper
import com.mifos.mobilewallet.model.domain.user.User
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.FetchClientData
import org.mifos.mobilewallet.core.domain.usecase.user.AuthenticateUser
import org.mifos.mobilewallet.core.domain.usecase.user.FetchUserDetails
import org.mifos.mobilewallet.core.utils.Constants
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mUsecaseHandler: UseCaseHandler,
    private val authenticateUserUseCase: AuthenticateUser,
    private val fetchClientDataUseCase: FetchClientData,
    private var fetchUserDetailsUseCase: FetchUserDetails,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.None)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginUser(username: String?, password: String?) {
        authenticateUserUseCase.walletRequestValues = AuthenticateUser.RequestValues(username, password)
        val requestValue = authenticateUserUseCase.walletRequestValues
        mUsecaseHandler.execute(authenticateUserUseCase, requestValue,
            object : UseCase.UseCaseCallback<AuthenticateUser.ResponseValue> {
                override fun onSuccess(response: AuthenticateUser.ResponseValue) {
                    createAuthenticatedService(response.user)
                    fetchClientData()
                    fetchUserDetails(response.user)
                }

                override fun onError(message: String) {
                    _uiState.value = LoginUiState.Error(message)
                }
            })
    }

    private fun fetchUserDetails(user: User) {
        mUsecaseHandler.execute(fetchUserDetailsUseCase,
            FetchUserDetails.RequestValues(user.userId),
            object : UseCase.UseCaseCallback<FetchUserDetails.ResponseValue> {
                override fun onSuccess(response: FetchUserDetails.ResponseValue) {
                    saveUserDetails(user, response.userWithRole)
                }

                override fun onError(message: String) {
                    //DebugUtil.log(message)
                }
            })
    }

    private fun fetchClientData() {
        mUsecaseHandler.execute(fetchClientDataUseCase, null,
            object : UseCase.UseCaseCallback<FetchClientData.ResponseValue> {
                override fun onSuccess(response: FetchClientData.ResponseValue) {
                    saveClientDetails(response.userDetails)
                    if (response.userDetails.name != "") {
                        _uiState.value = LoginUiState.Success
                    }
                }

                override fun onError(message: String) {}
            })
    }

    private fun createAuthenticatedService(user: User) {
        val authToken = Constants.BASIC +
                user.authenticationKey
        preferencesHelper.saveToken(authToken)
        //FineractApiManager.createSelfService(preferencesHelper.token)
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

    private fun saveClientDetails(client: com.mifos.mobilewallet.model.domain.client.Client) {
        preferencesHelper.saveFullName(client.name)
        preferencesHelper.clientId = client.clientId
        preferencesHelper.saveMobile(client.mobileNo)
    }

    // Represents different states for the LatestNews screen
    sealed interface LoginUiState {
        data object None: LoginUiState
        data object Loading: LoginUiState
        data object Success : LoginUiState
        data class Error(val exception: String) : LoginUiState
    }
}