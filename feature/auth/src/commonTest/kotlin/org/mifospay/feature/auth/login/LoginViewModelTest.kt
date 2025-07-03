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
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.domain.LoginUseCase
import org.mifospay.feature.auth.fakes.FakeAuthenticationRepository
import org.mifospay.feature.auth.fakes.fakeClient
import org.mifospay.feature.auth.login.LoginAction
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

    private lateinit var viewModel: LoginViewModel

    private val mockClientRepository = mock<ClientRepository> {
        everySuspend { getClient(any()) } returns DataState.Success(fakeClient)
    }

    private val mockUserPreferencesRepository = mock<UserPreferencesRepository> {
        everySuspend { updateToken(any()) } returns DataState.Success(Unit)
        everySuspend { updateClientInfo(any()) } returns DataState.Success(Unit)
        everySuspend { updateUserInfo(any()) } returns DataState.Success(Unit)
    }

    private var fakeAuthRepository = FakeAuthenticationRepository(shouldSucceed = true)
    private lateinit var loginUseCase: LoginUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        loginUseCase = LoginUseCase(
            repository = fakeAuthRepository,
            ioDispatcher = testDispatcher,
            clientRepository = mockClientRepository,
            userPreferencesRepository = mockUserPreferencesRepository,
        )

        viewModel = LoginViewModel(
            loginUseCase = loginUseCase,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginViewModel_InitialState_ValidInitialConditions(): TestResult {
        return runTest(testDispatcher) {
            assertEquals("", viewModel.stateFlow.value.username)
            assertEquals("", viewModel.stateFlow.value.password)
            assertFalse(viewModel.stateFlow.value.isPasswordVisible)
            assertEquals(null, viewModel.stateFlow.value.dialogState)
        }
    }

    @Test
    fun loginViewModel_UsernameChanged_UpdatesUsernameState() = runTest(testDispatcher) {
        viewModel.trySendAction(LoginAction.UsernameChanged("alice"))
        advanceUntilIdle()
        assertEquals("alice", viewModel.stateFlow.value.username)
    }

    @Test
    fun loginViewModel_PasswordChanged_UpdatesPasswordState() = runTest(testDispatcher) {
        viewModel.trySendAction(LoginAction.PasswordChanged("secret"))
        advanceUntilIdle()
        assertEquals("secret", viewModel.stateFlow.value.password)
    }

    @Test
    fun loginViewModel_TogglePasswordVisibility_UpdatesVisibilityState() = runTest(testDispatcher) {
        assertFalse(viewModel.stateFlow.value.isPasswordVisible)

        viewModel.trySendAction(LoginAction.TogglePasswordVisibility)
        advanceUntilIdle()
        assertTrue(viewModel.stateFlow.value.isPasswordVisible)

        viewModel.trySendAction(LoginAction.TogglePasswordVisibility)
        advanceUntilIdle()
        assertFalse(viewModel.stateFlow.value.isPasswordVisible)
    }

    @Test
    fun loginViewModel_IncorrectCredentials_ShowsErrorDialog() = runTest(testDispatcher) {
        fakeAuthRepository = FakeAuthenticationRepository(shouldSucceed = false)
        loginUseCase = LoginUseCase(
            repository = fakeAuthRepository,
            ioDispatcher = testDispatcher,
            clientRepository = mockClientRepository,
            userPreferencesRepository = mockUserPreferencesRepository,
        )
        viewModel = LoginViewModel(loginUseCase, SavedStateHandle())

        viewModel.trySendAction(LoginAction.UsernameChanged("testuser"))
        viewModel.trySendAction(LoginAction.PasswordChanged("testpass"))
        viewModel.trySendAction(LoginAction.LoginClicked)

        advanceUntilIdle()

        assertIs<LoginState.DialogState.Error>(viewModel.stateFlow.value.dialogState)

        assertEquals("Invalid Credentials", (viewModel.stateFlow.value.dialogState as LoginState.DialogState.Error).message)
    }

    @Test
    fun loginViewModel_CorrectCredentials_ShowsNoError() = runTest(testDispatcher) {
        viewModel.trySendAction(LoginAction.UsernameChanged("testuser"))
        viewModel.trySendAction(LoginAction.PasswordChanged("testpass"))
        viewModel.trySendAction(LoginAction.LoginClicked)

        advanceUntilIdle()

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    @Test
    fun loginViewModel_DismissDialog_dialogShouldBeNull() = runTest(testDispatcher) {
        fakeAuthRepository = FakeAuthenticationRepository(shouldSucceed = false)
        loginUseCase = LoginUseCase(
            repository = fakeAuthRepository,
            ioDispatcher = testDispatcher,
            clientRepository = mockClientRepository,
            userPreferencesRepository = mockUserPreferencesRepository,
        )
        viewModel = LoginViewModel(loginUseCase, SavedStateHandle())

        viewModel.trySendAction(LoginAction.UsernameChanged("testuser"))
        viewModel.trySendAction(LoginAction.PasswordChanged("wrongpass"))
        viewModel.trySendAction(LoginAction.LoginClicked)
        advanceUntilIdle()

        assertIs<LoginState.DialogState.Error>(viewModel.stateFlow.value.dialogState)

        viewModel.trySendAction(LoginAction.ErrorDialogDismiss)
        advanceUntilIdle()

        assertNull(viewModel.stateFlow.value.dialogState)
    }
}
