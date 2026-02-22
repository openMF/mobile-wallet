package org.mifospay.feature.auth.chooseAuthOption

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.authenticator.biometrics.platformAuthenticator.PlatformAuthenticationProvider
import org.mifos.authenticator.biometrics.platformAuthenticator.RegistrationResult
import template.core.base.ui.BaseViewModel

const val USER_ID = "mifosUser"
const val USER_EMAIL = "mifos@mifos.org"
const val DISPLAY_NAME = "XYZ"

class ChooseAuthOptionScreenViewmodel(
    private val chooseAuthOptionRepository: ChooseAuthOptionRepository,
) : BaseViewModel<
        ChooseAuthOptionScreenUiState,
        ChooseAuthOptionScreenEvents,
        ChooseAuthOptionScreenActions
>(ChooseAuthOptionScreenUiState()) {

    private fun setRegistrationResultNull() {
        mutableStateFlow.update {
            it.copy(
                registrationResult = null
            )
        }
    }

    private fun registerUser(
        platformAuthenticationProvider: PlatformAuthenticationProvider,
        userID: String = "",
        userEmail: String = "",
        displayName: String = "",
    ) {
        viewModelScope.launch {
            val registrationResult = platformAuthenticationProvider.registerUser(
                userID,
                userEmail,
                displayName,
            )

            mutableStateFlow.update {
                it.copy(
                    registrationResult = registrationResult
                )
            }

            when(registrationResult) {
                is RegistrationResult.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogBoxType = DialogBoxType.ERROR,
                            dialogBoxMessage = registrationResult.message
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotAvailable -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogBoxType = DialogBoxType.NOT_AVAILABLE,
                            dialogBoxMessage = "Option Not available"
                        )
                    }
                }
                RegistrationResult.PlatformAuthenticatorNotSet -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogBoxType = DialogBoxType.NOT_SET,
                            dialogBoxMessage = "Platform authenticator not set."
                        )
                    }
                }
                is RegistrationResult.Success -> {
                    saveAppLockOption(AppLockOption.DeviceLock)
                    saveRegistrationData(registrationResult.message)
                    sendEvent(ChooseAuthOptionScreenEvents.BiometricRegistrationSuccess)
                    setRegistrationResultNull()
                }
            }
        }
    }

    private fun saveRegistrationData(registrationData: String) =
        chooseAuthOptionRepository.saveRegistrationData(registrationData)


    private fun saveAppLockOption(appLock: AppLockOption) {
        chooseAuthOptionRepository.setAuthOption(appLock)
    }

    override fun handleAction(action: ChooseAuthOptionScreenActions) {
        when (action) {
            ChooseAuthOptionScreenActions.OnSelectDeviceLock -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedAuthOption = AppLockOption.DeviceLock
                    )
                }
            }
            ChooseAuthOptionScreenActions.OnSelectPasscode -> {
                mutableStateFlow.update {
                    it.copy(selectedAuthOption = AppLockOption.MifosPasscode)
                }
                saveAppLockOption(AppLockOption.MifosPasscode)
            }
            is ChooseAuthOptionScreenActions.RegisterUserBiometrics -> {
                registerUser(
                    platformAuthenticationProvider = action.platformAuthenticationProvider,
                    USER_ID,
                    USER_EMAIL,
                    DISPLAY_NAME,
                )
            }

            ChooseAuthOptionScreenActions.DismissDialogBox -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogBoxType = DialogBoxType.None
                    )
                }
            }

            is ChooseAuthOptionScreenActions.SetupPlatformAuthenticator -> {
                action.platformAuthenticationProvider.setupPlatformAuthenticator()
            }
        }
    }
}

data class ChooseAuthOptionScreenUiState(
    val registrationResult: RegistrationResult? = null,
    val dialogBoxType: DialogBoxType = DialogBoxType.None,
    val dialogBoxMessage: String = "",
    val selectedAuthOption: AppLockOption = AppLockOption.None,
)



sealed interface ChooseAuthOptionScreenEvents {
    data object BiometricRegistrationSuccess: ChooseAuthOptionScreenEvents
    data object OnChoosePasscode: ChooseAuthOptionScreenEvents

}

sealed interface ChooseAuthOptionScreenActions {
    data class SetupPlatformAuthenticator(val platformAuthenticationProvider: PlatformAuthenticationProvider): ChooseAuthOptionScreenActions
    data class RegisterUserBiometrics(val platformAuthenticationProvider: PlatformAuthenticationProvider): ChooseAuthOptionScreenActions
    data object OnSelectDeviceLock: ChooseAuthOptionScreenActions
    data object OnSelectPasscode: ChooseAuthOptionScreenActions
    data object DismissDialogBox: ChooseAuthOptionScreenActions

}

enum class DialogBoxType {
    ERROR,
    NOT_SET,
    NOT_AVAILABLE,
    None,
}
