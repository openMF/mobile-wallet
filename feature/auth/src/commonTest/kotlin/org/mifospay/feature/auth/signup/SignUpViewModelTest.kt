/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.signup

import androidx.lifecycle.SavedStateHandle
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.SearchRepository
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.model.search.SearchResult
import org.mifospay.core.ui.utils.PasswordStrength
import org.mifospay.core.ui.utils.PasswordStrengthResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SignUpViewModelTest {

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val mockUserRepository: UserRepository = mock()
    private val mockSearchRepository: SearchRepository = mock()
    private val mockClientRepository: ClientRepository = mock()

    private lateinit var viewModel: SignupViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        everySuspend {
            mockSearchRepository.searchResources(
                matching { it == "alice.smith" || it == "9876543210" },
                any(),
                any(),
            )
        } returns DataState.Success(emptyList())

        everySuspend {
            mockSearchRepository.searchResources(
                matching { it == "failure" || it == "1234567890" }, any(), any(),
            )
        } returns DataState.Success(
            listOf(
                SearchResult(
                    entityId = 1,
                    entityAccountNo = "123",
                    entityName = "Alice",
                    entityType = "savings",
                    parentId = 1,
                    parentName = "smith",
                ),
                (
                    SearchResult(
                        entityId = 2,
                        entityAccountNo = "123",
                        entityName = "Alice",
                        entityType = "savings",
                        parentId = 1,
                        parentName = "smith",
                    )
                    ),
            ),
        )
        everySuspend { mockUserRepository.createUser(any()) } returns DataState.Success(123)
        everySuspend { mockClientRepository.createClient(any()) } returns DataState.Success(456)
        everySuspend { mockUserRepository.assignClientToUser(any(), any()) } returns DataState.Success(Unit)

        viewModel = SignupViewModel(
            userRepository = mockUserRepository,
            searchRepository = mockSearchRepository,
            clientRepository = mockClientRepository,
            savedStateHandle = SavedStateHandle(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun signUpViewModel_SuccessfulSubmission_CreatesUserAndClient() = runTest(testDispatcher) {
        viewModel.trySendAction(SignUpAction.FirstNameInputChange("Alice"))
        viewModel.trySendAction(SignUpAction.LastNameInputChange("Smith"))
        viewModel.trySendAction(SignUpAction.UserNameInputChange("alice.smith"))
        viewModel.trySendAction(SignUpAction.EmailInputChange("alice@example.com"))
        viewModel.trySendAction(SignUpAction.MobileNumberInputChange("9876543210"))
        viewModel.trySendAction(SignUpAction.PasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.ConfirmPasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.AddressLine1InputChange("123 Main Street"))
        viewModel.trySendAction(SignUpAction.AddressLine2InputChange("Apt 4B"))
        viewModel.trySendAction(SignUpAction.PinCodeInputChange("560001"))
        viewModel.trySendAction(SignUpAction.CountryInputChange("IN"))
        viewModel.trySendAction(SignUpAction.StateInputChange("KA"))
        viewModel.trySendAction(SignUpAction.SavingsAccountNoInputChange(7))

        // Simulate strong password result
        viewModel.trySendAction(
            SignUpAction.Internal.ReceivePasswordStrengthResult(
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5),
            ),
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    @Test
    fun signUpViewModel_UsernameAlreadyExists_ShowsErrorDialog() = runTest(testDispatcher) {
        viewModel.trySendAction(SignUpAction.FirstNameInputChange("Alice"))
        viewModel.trySendAction(SignUpAction.LastNameInputChange("Smith"))
        viewModel.trySendAction(SignUpAction.UserNameInputChange("failure"))
        viewModel.trySendAction(SignUpAction.EmailInputChange("alice@example.com"))
        viewModel.trySendAction(SignUpAction.MobileNumberInputChange("9876543210"))
        viewModel.trySendAction(SignUpAction.PasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.ConfirmPasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.AddressLine1InputChange("123 Main Street"))
        viewModel.trySendAction(SignUpAction.AddressLine2InputChange("Apt 4B"))
        viewModel.trySendAction(SignUpAction.PinCodeInputChange("560001"))
        viewModel.trySendAction(SignUpAction.CountryInputChange("IN"))
        viewModel.trySendAction(SignUpAction.StateInputChange("KA"))
        viewModel.trySendAction(SignUpAction.SavingsAccountNoInputChange(7))

        // Simulate strong password result
        viewModel.trySendAction(
            SignUpAction.Internal.ReceivePasswordStrengthResult(
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5),
            ),
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertEquals("Error(message=Username already exists)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_MobileNumberAlreadyExists_ShowsErrorDialog() = runTest(testDispatcher) {
        viewModel.trySendAction(SignUpAction.FirstNameInputChange("Alice"))
        viewModel.trySendAction(SignUpAction.LastNameInputChange("Smith"))
        viewModel.trySendAction(SignUpAction.UserNameInputChange("alice.smith"))
        viewModel.trySendAction(SignUpAction.EmailInputChange("alice@example.com"))
        viewModel.trySendAction(SignUpAction.MobileNumberInputChange("1234567890"))
        viewModel.trySendAction(SignUpAction.PasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.ConfirmPasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.AddressLine1InputChange("123 Main Street"))
        viewModel.trySendAction(SignUpAction.AddressLine2InputChange("Apt 4B"))
        viewModel.trySendAction(SignUpAction.PinCodeInputChange("560001"))
        viewModel.trySendAction(SignUpAction.CountryInputChange("IN"))
        viewModel.trySendAction(SignUpAction.StateInputChange("KA"))
        viewModel.trySendAction(SignUpAction.SavingsAccountNoInputChange(7))

        viewModel.trySendAction(
            SignUpAction.Internal.ReceivePasswordStrengthResult(
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5),
            ),
        )

        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertEquals("Error(message=Mobile Number already exists)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_UsernameAndMobileAlreadyExist_ShowsCombinedErrorDialog() = runTest(testDispatcher) {
        viewModel.trySendAction(SignUpAction.FirstNameInputChange("Alice"))
        viewModel.trySendAction(SignUpAction.LastNameInputChange("Smith"))
        viewModel.trySendAction(SignUpAction.UserNameInputChange("failure"))
        viewModel.trySendAction(SignUpAction.EmailInputChange("alice@example.com"))
        viewModel.trySendAction(SignUpAction.MobileNumberInputChange("1234567890"))
        viewModel.trySendAction(SignUpAction.PasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.ConfirmPasswordInputChange("Strong@12345"))
        viewModel.trySendAction(SignUpAction.AddressLine1InputChange("123 Main Street"))
        viewModel.trySendAction(SignUpAction.AddressLine2InputChange("Apt 4B"))
        viewModel.trySendAction(SignUpAction.PinCodeInputChange("560001"))
        viewModel.trySendAction(SignUpAction.CountryInputChange("IN"))
        viewModel.trySendAction(SignUpAction.StateInputChange("KA"))
        viewModel.trySendAction(SignUpAction.SavingsAccountNoInputChange(7))

        viewModel.trySendAction(
            SignUpAction.Internal.ReceivePasswordStrengthResult(
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5),
            ),
        )

        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertEquals("Error(message=Username already exists\nMobile Number already exists)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_FirstNameMissing_ShowsFirstNameRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("firstName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your first name.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_LastNameMissing_ShowsLastNameRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("lastName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your last name.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_UsernameMissing_ShowsUsernameRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("userName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your username.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_EmailMissing_ShowsEmailRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("email"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your email.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_MobileNumberMissing_ShowsMobileNumberRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("mobileNumber"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter a your mobile number.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_PasswordMissing_ShowsPasswordMinLengthError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("password"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Password must be at least 12 characters long.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_ConfirmPasswordMissing_ShowsPasswordMismatchError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("confirmPassword"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Passwords do not match.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_AddressLine1Missing_ShowsAddressLine1RequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("addressLine1"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your address line 1.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_AddressLine2Missing_ShowsAddressLine2RequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("addressLine2"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your address line 2.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_PinCodeMissing_ShowsPinCodeRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("pinCode"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your pincode.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_PinCodeLengthLower_ShowsPinCodeLengthError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("pinCode"))
        viewModel.trySendAction(SignUpAction.PinCodeInputChange("56000"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Pin code must be 6 digits long.)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_CountryMissing_ShowsCountryRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("country"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your country)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_StateMissing_ShowsStateRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("state"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please enter your state)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun signUpViewModel_SavingsAccountMissing_ShowsSavingsAccountRequiredError() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("savingsAccountNo"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=Please select a savings account.)", viewModel.stateFlow.value.dialogState.toString())
    }

    fun fillValidSignUpFormExcept(
        exclude: Set<String> = emptySet(),
    ) {
        if ("firstName" !in exclude) {
            viewModel.trySendAction(SignUpAction.FirstNameInputChange("Alice"))
        }
        if ("lastName" !in exclude) {
            viewModel.trySendAction(SignUpAction.LastNameInputChange("Smith"))
        }
        if ("userName" !in exclude) {
            viewModel.trySendAction(SignUpAction.UserNameInputChange("alice.smith"))
        }
        if ("email" !in exclude) {
            viewModel.trySendAction(SignUpAction.EmailInputChange("alice@example.com"))
        }
        if ("mobileNumber" !in exclude) {
            viewModel.trySendAction(SignUpAction.MobileNumberInputChange("9876543210"))
        }
        if ("password" !in exclude) {
            viewModel.trySendAction(SignUpAction.PasswordInputChange("Strong@12345"))
        }
        if ("confirmPassword" !in exclude) {
            viewModel.trySendAction(SignUpAction.ConfirmPasswordInputChange("Strong@12345"))
        }
        if ("addressLine1" !in exclude) {
            viewModel.trySendAction(SignUpAction.AddressLine1InputChange("123 Main Street"))
        }
        if ("addressLine2" !in exclude) {
            viewModel.trySendAction(SignUpAction.AddressLine2InputChange("Apt 4B"))
        }
        if ("pinCode" !in exclude) {
            viewModel.trySendAction(SignUpAction.PinCodeInputChange("560001"))
        }
        if ("country" !in exclude) {
            viewModel.trySendAction(SignUpAction.CountryInputChange("IN"))
        }
        if ("state" !in exclude) {
            viewModel.trySendAction(SignUpAction.StateInputChange("KA"))
        }
        if ("savingsAccountNo" !in exclude) {
            viewModel.trySendAction(SignUpAction.SavingsAccountNoInputChange(7))
        }

        if ("password" !in exclude && "confirmPassword" !in exclude) {
            viewModel.trySendAction(
                SignUpAction.Internal.ReceivePasswordStrengthResult(
                    PasswordStrengthResult.Success(PasswordStrength.LEVEL_5),
                ),
            )
        }
    }
}
