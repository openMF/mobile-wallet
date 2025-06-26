package org.mifospay.feature.auth.signup

import androidx.lifecycle.SavedStateHandle
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.SearchRepository
import org.mifospay.core.data.repository.UserRepository
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.data.util.Constants.ENTER_ADDRESS_LINE_1
import org.mifospay.core.data.util.Constants.ENTER_ADDRESS_LINE_2
import org.mifospay.core.data.util.Constants.ENTER_COUNTRY
import org.mifospay.core.data.util.Constants.ENTER_EMAIL
import org.mifospay.core.data.util.Constants.ENTER_FIRST_NAME
import org.mifospay.core.data.util.Constants.ENTER_LAST_NAME
import org.mifospay.core.data.util.Constants.ENTER_MOBILE
import org.mifospay.core.data.util.Constants.ENTER_PINCODE
import org.mifospay.core.data.util.Constants.ENTER_STATE
import org.mifospay.core.data.util.Constants.ENTER_USERNAME
import org.mifospay.core.data.util.Constants.PASSWORD_MISMATCH
import org.mifospay.core.data.util.Constants.SELECT_SAVINGS_ACCOUNT
import org.mifospay.core.data.util.Constants.alreadyExists
import org.mifospay.core.data.util.Constants.passwordMinLength
import org.mifospay.core.model.search.SearchResult
import org.mifospay.core.ui.utils.PasswordStrength
import org.mifospay.core.ui.utils.PasswordStrengthResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val mockUserRepository: UserRepository = mock()
    private val mockSearchRepository: SearchRepository = mock()
    private val mockClientRepository: ClientRepository = mock()

    private lateinit var viewModel: SignupViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)


        everySuspend { mockSearchRepository.searchResources(
            matching { it == "alice.smith" || it == "9876543210" },
            any(),
            any()
        )} returns DataState.Success(emptyList())

        everySuspend { mockSearchRepository.searchResources(
            matching { it == "failure" || it == "1234567890" }
            , any(), any()) } returns DataState.Success(listOf(SearchResult(
            entityId = 1,
            entityAccountNo = "123",
            entityName = "Alice",
            entityType = "savings",
            parentId = 1,
            parentName = "smith"
        ),
            (SearchResult(
                entityId = 2,
                entityAccountNo = "123",
                entityName = "Alice",
                entityType = "savings",
                parentId = 1,
                parentName = "smith"
            ))
        )
        )
        everySuspend { mockUserRepository.createUser(any()) } returns DataState.Success(123)
        everySuspend { mockClientRepository.createClient(any()) } returns DataState.Success(456)
        everySuspend { mockUserRepository.assignClientToUser(any(), any()) } returns DataState.Success(Unit)

        viewModel = SignupViewModel(
            userRepository = mockUserRepository,
            searchRepository = mockSearchRepository,
            clientRepository = mockClientRepository,
            savedStateHandle = SavedStateHandle()
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSuccessfulSignUp() = runTest(testDispatcher) {
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
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5)
            )
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertNull(viewModel.stateFlow.value.dialogState)
    }

    @Test
    fun testUsernameAlreadyExists() = runTest(testDispatcher) {
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
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5)
            )
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertEquals("Error(message=${ alreadyExists("Username") })",  viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testMobileNoAlreadyExists() = runTest(testDispatcher) {
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

        // Simulate strong password result
        viewModel.trySendAction(
            SignUpAction.Internal.ReceivePasswordStrengthResult(
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5)
            )
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertEquals("Error(message=${ alreadyExists("Mobile Number") })",  viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testUsernameAndMobileNoAlreadyExists() = runTest(testDispatcher) {
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

        // Simulate strong password result
        viewModel.trySendAction(
            SignUpAction.Internal.ReceivePasswordStrengthResult(
                PasswordStrengthResult.Success(PasswordStrength.LEVEL_5)
            )
        )

        // Submit
        viewModel.trySendAction(SignUpAction.SubmitClick)

        advanceUntilIdle()

        assertEquals("Error(message=${ alreadyExists("Username") }\n${alreadyExists("Mobile Number")})",  viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingFirstName() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("firstName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_FIRST_NAME)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingLastName() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("lastName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_LAST_NAME)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingUserName() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("userName"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_USERNAME)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingEmail() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("email"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_EMAIL)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingMobileNumber() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("mobileNumber"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_MOBILE)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingPassword() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("password"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=${passwordMinLength(12)})", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingConfirmPassword() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("confirmPassword"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$PASSWORD_MISMATCH)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingAddressLine1() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("addressLine1"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_ADDRESS_LINE_1)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingAddressLine2() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("addressLine2"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_ADDRESS_LINE_2)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingPinCode() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("pinCode"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_PINCODE)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingCountry() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("country"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_COUNTRY)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingState() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("state"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$ENTER_STATE)", viewModel.stateFlow.value.dialogState.toString())
    }

    @Test
    fun testSignUpMissingSavingsAccount() = runTest(testDispatcher) {
        fillValidSignUpFormExcept(setOf("savingsAccountNo"))
        viewModel.trySendAction(SignUpAction.SubmitClick)
        advanceUntilIdle()
        assertEquals("Error(message=$SELECT_SAVINGS_ACCOUNT)", viewModel.stateFlow.value.dialogState.toString())
    }



    fun fillValidSignUpFormExcept(
        exclude: Set<String> = emptySet()
    ) {
        if ("firstName" !in exclude)
            viewModel.trySendAction(SignUpAction.FirstNameInputChange("Alice"))
        if ("lastName" !in exclude)
            viewModel.trySendAction(SignUpAction.LastNameInputChange("Smith"))
        if ("userName" !in exclude)
            viewModel.trySendAction(SignUpAction.UserNameInputChange("alice.smith"))
        if ("email" !in exclude)
            viewModel.trySendAction(SignUpAction.EmailInputChange("alice@example.com"))
        if ("mobileNumber" !in exclude)
            viewModel.trySendAction(SignUpAction.MobileNumberInputChange("9876543210"))
        if ("password" !in exclude)
            viewModel.trySendAction(SignUpAction.PasswordInputChange("Strong@12345"))
        if ("confirmPassword" !in exclude)
            viewModel.trySendAction(SignUpAction.ConfirmPasswordInputChange("Strong@12345"))
        if ("addressLine1" !in exclude)
            viewModel.trySendAction(SignUpAction.AddressLine1InputChange("123 Main Street"))
        if ("addressLine2" !in exclude)
            viewModel.trySendAction(SignUpAction.AddressLine2InputChange("Apt 4B"))
        if ("pinCode" !in exclude)
            viewModel.trySendAction(SignUpAction.PinCodeInputChange("560001"))
        if ("country" !in exclude)
            viewModel.trySendAction(SignUpAction.CountryInputChange("IN"))
        if ("state" !in exclude)
            viewModel.trySendAction(SignUpAction.StateInputChange("KA"))
        if ("savingsAccountNo" !in exclude)
            viewModel.trySendAction(SignUpAction.SavingsAccountNoInputChange(7))

        if ("password" !in exclude && "confirmPassword" !in exclude) {
            viewModel.trySendAction(
                SignUpAction.Internal.ReceivePasswordStrengthResult(
                    PasswordStrengthResult.Success(PasswordStrength.LEVEL_5)
                )
            )
        }
    }


}
