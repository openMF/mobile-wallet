package org.mifospay.feature.auth.mobileVerify

import androidx.lifecycle.SavedStateHandle
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SearchRepository
import org.mifospay.core.model.search.SearchResult
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MobileVerificationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchRepository: SearchRepository
    private lateinit var viewModel: MobileVerificationViewModel

    val failMobileNo = "1234567890"
    val validMobileNo = "9876543210"

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchRepository = mock<SearchRepository>(){
            everySuspend { searchResources(validMobileNo, "clients", true) }returns  DataState.Success(data = emptyList())
            everySuspend { searchResources(failMobileNo, "clients", true) } returns DataState.Success(data = listOf(
                SearchResult(
                    entityId=1,
                    entityAccountNo = "abc",
                    entityName="abc",
                    entityType="saving",
                    parentId=1,
                    parentName="def",
                )
            ))

        }
        viewModel = MobileVerificationViewModel(
            searchRepository = searchRepository,
            savedStateHandle = SavedStateHandle()
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Invalid phone number shows validation error`() = runTest(testDispatcher) {
        val invalidPhoneNo = "123"

        // Act
        viewModel.trySendAction(MobileVerificationAction.PhoneNoChanged(invalidPhoneNo))
        viewModel.trySendAction(MobileVerificationAction.VerifyPhoneBtnClicked)

        advanceUntilIdle()

        // Assert
        val currentState = viewModel.stateFlow.value
        assertTrue(currentState is MobileVerificationState.VerifyPhoneState)
        val dialogState = (currentState as MobileVerificationState.VerifyPhoneState).dialogState
        assertTrue(dialogState is MobileVerificationState.DialogState.Error)
        assertTrue((dialogState as MobileVerificationState.DialogState.Error).message.contains("isn't valid"))
    }


    @Test
    fun `Phone number already exists shows error dialog`() = runTest(testDispatcher) {

        viewModel.trySendAction(MobileVerificationAction.PhoneNoChanged(failMobileNo))
        viewModel.trySendAction(MobileVerificationAction.VerifyPhoneBtnClicked)

        advanceUntilIdle()

        val currentState = viewModel.stateFlow.value
        assertTrue(currentState is MobileVerificationState.VerifyPhoneState)
        val errorMessage = (currentState as MobileVerificationState.VerifyPhoneState).dialogState
        assertTrue(errorMessage is MobileVerificationState.DialogState.Error)
        assertTrue((errorMessage as MobileVerificationState.DialogState.Error).message.contains("already exists"))
    }

    @Test
    fun `Valid phone number with no existing user proceeds to OTP screen`() = runTest(testDispatcher) {

        // Act
        viewModel.trySendAction(MobileVerificationAction.PhoneNoChanged(validMobileNo))
        viewModel.trySendAction(MobileVerificationAction.VerifyPhoneBtnClicked)

        advanceUntilIdle()

        // Assert
        val currentState = viewModel.stateFlow.value
        assertTrue(currentState is MobileVerificationState.VerifyOtpState)
        assertTrue((currentState as MobileVerificationState.VerifyOtpState).phoneNo == validMobileNo)
    }

}
