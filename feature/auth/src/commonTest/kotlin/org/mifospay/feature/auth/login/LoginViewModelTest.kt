/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.AuthenticationRepository
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.domain.LoginUseCase
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.model.user.UserInfo
import org.mifospay.core.network.config.InstanceConfigManager
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

    // `LoginUseCase` is a final class (the `@OpenForMokkery` all-open plugin is not
    // wired in this module), so it cannot be `mock()`ed. Build a REAL use-case over
    // mocked collaborators and stub the underlying repository calls instead.
    private val authenticationRepository: AuthenticationRepository = mock()
    private val clientRepository: ClientRepository = mock()
    private val userPreferencesRepository: UserPreferencesRepository = mock {
        every { selectedInstance } returns MutableStateFlow<ServerInstance?>(null)
        every { selectedInterbankInstance } returns MutableStateFlow<InterbankServer?>(null)
    }

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        loginUseCase = LoginUseCase(
            repository = authenticationRepository,
            clientRepository = clientRepository,
            userPreferencesRepository = userPreferencesRepository,
            ioDispatcher = testDispatcher,
        )

        viewModel = LoginViewModel(
            loginUseCase = loginUseCase,
            instanceConfigManager = InstanceConfigManager(userPreferencesRepository),
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
     * Stubs the repository chain the real [LoginUseCase] walks and verifies the
     * authenticate call via [verifySuspend].
     */
    @Test
    fun givenCorrectCredentials_whenLoginClicked_thenNoErrorShown() = runTest {
        stubSuccessfulLogin(
            username = "Mifos",
            password = "MifosPassword",
            userInfo = validUserInfo(username = "abc"),
        )

        viewModel.trySendAction(LoginAction.UsernameChanged("Mifos"))
        viewModel.trySendAction(LoginAction.PasswordChanged("MifosPassword"))
        viewModel.trySendAction(LoginAction.LoginClicked)

        advanceUntilIdle()

        /*
         * Verifies that the authentication repository was hit with the correct credentials.
         */
        verifySuspend {
            authenticationRepository.authenticate("Mifos", "MifosPassword")
        }

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    /**
     * Tests that incorrect credentials show an error dialog with appropriate message.
     */
    @Test
    fun givenIncorrectCredentials_whenLoginClicked_thenErrorDialogShown() = runTest {
        /*
         * Mocks the authentication repository to fail — the use-case maps this to a
         * DataState.Error("Invalid credentials").
         */
        everySuspend {
            authenticationRepository.authenticate("Hekmat", "MyPassword")
        } returns DataState.Error(Exception("Invalid Credentials"))

        viewModel.trySendAction(LoginAction.UsernameChanged("Hekmat"))
        viewModel.trySendAction(LoginAction.PasswordChanged("MyPassword"))
        viewModel.trySendAction(LoginAction.LoginClicked)

        advanceUntilIdle()

        /*
         * Verifies that authentication was attempted with the provided wrong credentials.
         */
        verifySuspend {
            authenticationRepository.authenticate("Hekmat", "MyPassword")
        }

        val dialog = viewModel.stateFlow.value.dialogState
        assertIs<LoginState.DialogState.Error>(dialog)
        assertEquals("Invalid credentials", dialog.message)
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
        stubSuccessfulLogin(
            username = "validUser",
            password = "validPass",
            userInfo = validUserInfo(username = "validUser"),
        )

        viewModel.trySendAction(LoginAction.UsernameChanged("validUser"))
        viewModel.trySendAction(LoginAction.PasswordChanged("validPass"))
        viewModel.trySendAction(LoginAction.LoginClicked)
        advanceUntilIdle()

        /*
         * Verifies that authentication was attempted with the correct credentials.
         */
        verifySuspend {
            authenticationRepository.authenticate("validUser", "validPass")
        }

        viewModel.eventFlow.test {
            // Expect NavigateToMifosPasscodeScreen after successful login
            assertTrue(awaitItem() is LoginEvent.NavigateToMifosPasscodeScreen)
        }
    }

    /**
     * Stubs the full repository chain the real [LoginUseCase] walks on a successful login:
     * authenticate → updateToken → getClient → updateClientInfo → updateUserInfo.
     */
    private fun stubSuccessfulLogin(username: String, password: String, userInfo: UserInfo) {
        everySuspend {
            authenticationRepository.authenticate(username, password)
        } returns DataState.Success(userInfo)
        everySuspend { userPreferencesRepository.updateToken(any()) } returns DataState.Success(Unit)
        everySuspend { clientRepository.getClient(any()) } returns DataState.Success(testClient)
        everySuspend {
            userPreferencesRepository.updateClientInfo(any())
        } returns DataState.Success(Unit)
        everySuspend {
            userPreferencesRepository.updateUserInfo(any())
        } returns DataState.Success(Unit)
    }

    private fun validUserInfo(username: String): UserInfo = UserInfo(
        userId = 1,
        username = username,
        base64EncodedAuthenticationKey = "fake-auth-key",
        authenticated = true,
        officeId = 1,
        officeName = "Main Office",
        roles = emptyList(),
        permissions = emptyList(),
        clients = listOf(1),
        shouldRenewPassword = false,
        isTwoFactorAuthenticationRequired = false,
    )

    private val testClient = Client(
        id = 1,
        accountNo = "CLIENT001",
        externalId = "",
        active = true,
        activationDate = emptyList(),
        firstname = "John",
        lastname = "Doe",
        displayName = "John Doe",
        mobileNo = "+1234567890",
        emailAddress = "john@example.com",
        dateOfBirth = emptyList(),
        isStaff = false,
        officeId = 1,
        officeName = "Head Office",
        savingsProductName = "",
    )
}
