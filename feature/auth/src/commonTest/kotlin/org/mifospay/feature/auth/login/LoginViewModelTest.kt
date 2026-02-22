/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.domain.LoginUseCase
import org.mifospay.core.model.user.UserInfo
import org.mifospay.feature.auth.login.LoginAction
import org.mifospay.feature.auth.login.LoginEvent
import org.mifospay.feature.auth.login.LoginState
import org.mifospay.feature.auth.login.LoginViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val loginUseCase: LoginUseCase = mock()

    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        viewModel = LoginViewModel(
            loginUseCase = loginUseCase,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Verifies initial state values are set correctly on ViewModel init.
     */
    @Test
    fun givenInitialState_whenViewModelInitialized_thenValidInitialConditions() {
        assertEquals("", viewModel.stateFlow.value.username)
        assertEquals("", viewModel.stateFlow.value.password)
        assertFalse(viewModel.stateFlow.value.isPasswordVisible)
        assertEquals(null, viewModel.stateFlow.value.dialogState)
    }

    /**
     * Tests that the username input updates the ViewModel state correctly.
     */
    @Test
    fun givenUsernameInput_whenUsernameChanged_thenStateUpdated() = runTest {
        viewModel.trySendAction(LoginAction.UsernameChanged("alice"))
        advanceUntilIdle()
        assertEquals("alice", viewModel.stateFlow.value.username)
    }

    /**
     * Tests that the password input updates the ViewModel state correctly.
     */
    @Test
    fun givenPasswordInput_whenPasswordChanged_thenStateUpdated() = runTest {
        viewModel.trySendAction(LoginAction.PasswordChanged("secret"))
        advanceUntilIdle()
        assertEquals("secret", viewModel.stateFlow.value.password)
    }

    /**
     * Tests the toggle password visibility logic.
     */
    @Test
    fun whenTogglePasswordVisibility_thenVisibilityStateUpdated() = runTest {
        assertFalse(viewModel.stateFlow.value.isPasswordVisible)

        viewModel.trySendAction(LoginAction.TogglePasswordVisibility)
        advanceUntilIdle()
        assertTrue(viewModel.stateFlow.value.isPasswordVisible)

        viewModel.trySendAction(LoginAction.TogglePasswordVisibility)
        advanceUntilIdle()
        assertFalse(viewModel.stateFlow.value.isPasswordVisible)
    }

    /**
     * Tests that correct credentials result in a success state with no error dialog.
     *
     * Uses [everySuspend] to mock suspend call and [verifySuspend] to verify usage.
     */
    @Test
    fun givenCorrectCredentials_whenLoginClicked_thenNoErrorShown() = runTest {
        /*
         * Mocks the LoginUseCase to return a successful user info when invoked.
         */
        everySuspend {
            loginUseCase.invoke("Mifos", "MifosPassword")
        } returns DataState.Success(
            UserInfo(
                userId = 1,
                username = "abc",
                base64EncodedAuthenticationKey = "fake-auth-key",
                authenticated = true,
                officeId = 1,
                officeName = "Main Office",
                roles = emptyList(),
                permissions = emptyList(),
                clients = listOf(1),
                shouldRenewPassword = false,
                isTwoFactorAuthenticationRequired = false,
            ),
        )

        viewModel.trySendAction(LoginAction.UsernameChanged("Mifos"))
        viewModel.trySendAction(LoginAction.PasswordChanged("MifosPassword"))
        viewModel.trySendAction(LoginAction.LoginClicked)

        advanceUntilIdle()

        /*
         * Verifies that loginUseCase was invoked with the correct credentials.
         */
        verifySuspend {
            loginUseCase.invoke("Mifos", "MifosPassword")
        }

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    /**
     * Tests that incorrect credentials show an error dialog with appropriate message.
     */
    @Test
    fun givenIncorrectCredentials_whenLoginClicked_thenErrorDialogShown() = runTest {
        /*
         * Mocks the LoginUseCase to return DataState.Error for wrong credentials.
         */
        everySuspend {
            loginUseCase.invoke("Hekmat", "MyPassword")
        } returns DataState.Error(Exception("Invalid Credentials"))

        viewModel.trySendAction(LoginAction.UsernameChanged("Hekmat"))
        viewModel.trySendAction(LoginAction.PasswordChanged("MyPassword"))
        viewModel.trySendAction(LoginAction.LoginClicked)

        advanceUntilIdle()

        /*
         * Verifies that loginUseCase was invoked with the provided wrong credentials.
         */
        verifySuspend {
            loginUseCase.invoke("Hekmat", "MyPassword")
        }

        val dialog = viewModel.stateFlow.value.dialogState
        assertIs<LoginState.DialogState.Error>(dialog)
        assertEquals("Invalid Credentials", dialog.message)
    }

    /**
     * Tests that signup click triggers navigation to signup screen.
     */
    @Test
    fun whenSignupClicked_thenNavigateToSignupEventEmitted() = runTest {
        viewModel.eventFlow.test {
            viewModel.trySendAction(LoginAction.SignupClicked)
            assertIs<LoginEvent.NavigateToSignup>(awaitItem())
        }
    }

    /**
     * Tests that successful login triggers navigation to passcode screen.
     */
    @Test
    fun givenCorrectCredentials_whenLoginSucceeds_thenNavigateToPasscodeScreenEmitted() = runTest {
        everySuspend {
            loginUseCase.invoke("validUser", "validPass")
        } returns DataState.Success(
            UserInfo(
                userId = 1,
                username = "validUser",
                base64EncodedAuthenticationKey = "auth",
                authenticated = true,
                officeId = 1,
                officeName = "HQ",
                roles = emptyList(),
                permissions = emptyList(),
                clients = listOf(1),
                shouldRenewPassword = false,
                isTwoFactorAuthenticationRequired = false,
            ),
        )

        viewModel.trySendAction(LoginAction.UsernameChanged("validUser"))
        viewModel.trySendAction(LoginAction.PasswordChanged("validPass"))
        viewModel.trySendAction(LoginAction.LoginClicked)
        advanceUntilIdle()

        /*
         * Verifies that loginUseCase was invoked with the correct credentials.
         */
        verifySuspend {
            loginUseCase.invoke("validUser", "validPass")
        }

        viewModel.eventFlow.test {
            // Expect NavigateToPasscodeScreen after successful login
            assertTrue(awaitItem() is LoginEvent.NavigateToChooseAuthOptionScreen)
        }
    }
}
