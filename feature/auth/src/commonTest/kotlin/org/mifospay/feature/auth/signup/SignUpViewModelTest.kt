package org.mifospay.feature.auth.signup

import androidx.lifecycle.SavedStateHandle
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.model.search.SearchResult
import org.mifospay.core.ui.utils.PasswordStrength
import org.mifospay.core.ui.utils.PasswordStrengthResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SignUpViewModelTest {


    private val mockUserRepository: UserRepository = mock()
    private val mockSearchRepository: SearchRepository = mock()
    private val mockClientRepository: ClientRepository = mock()

    private lateinit var viewModel: SignupViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

            matching { it == "alice.smith" || it == "9876543210" },
            any(),

            entityId = 1,
            entityAccountNo = "123",
            entityName = "Alice",
            entityType = "savings",
            parentId = 1,
        ),
                entityId = 2,
                entityAccountNo = "123",
                entityName = "Alice",
                entityType = "savings",
                parentId = 1,
        )
        )
        everySuspend { mockUserRepository.createUser(any()) } returns DataState.Success(123)
        everySuspend { mockClientRepository.createClient(any()) } returns DataState.Success(456)
        everySuspend { mockUserRepository.assignClientToUser(any(), any()) } returns DataState.Success(Unit)

        viewModel = SignupViewModel(
            userRepository = mockUserRepository,
            searchRepository = mockSearchRepository,
            clientRepository = mockClientRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
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
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    @Test
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
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

    }

    @Test
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
        )

        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

    }

    @Test
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
        )

        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

    }

    @Test
        fillValidSignUpFormExcept(setOf("firstName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("lastName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("userName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("email"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("mobileNumber"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("password"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("confirmPassword"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("addressLine1"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("addressLine2"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("pinCode"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("country"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("state"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    @Test
        fillValidSignUpFormExcept(setOf("savingsAccountNo"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
    }

    fun fillValidSignUpFormExcept(
    ) {
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

        if ("password" !in exclude && "confirmPassword" !in exclude) {
            viewModel.trySendAction(
                SignUpAction.Internal.ReceivePasswordStrengthResult(
            )
        }
    }
}
