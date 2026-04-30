/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.intrabank.confirm

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.data.repository.UserVerificationRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.NewClient
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.TPTResponse
import org.mifospay.core.network.model.entity.client.ClientAccountsEntity
import org.mifospay.core.network.model.entity.payload.TransferPayload
import org.mifospay.core.network.model.entity.templates.account.AccountOptionsTemplate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransferConfirmStateTest {
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun amountIsValidReturnsFalseForZeroAmount() {
        val state = TransferConfirmState(
            amount = "0",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsFalseForNegativeAmount() {
        val state = TransferConfirmState(
            amount = "-1",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsTrueForPositiveAmountWithinBalance() {
        val state = TransferConfirmState(
            amount = "10.50",
            selectedAccountBalance = 100.0,
        )

        assertTrue(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsFalseWhenAmountExceedsBalance() {
        val state = TransferConfirmState(
            amount = "101",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun amountIsValidReturnsFalseForNonNumericAmount() {
        val state = TransferConfirmState(
            amount = "abc",
            selectedAccountBalance = 100.0,
        )

        assertFalse(state.amountIsValid)
    }

    @Test
    fun initiateTransferWithInvalidAmountClearsProcessing() = runTest {
        val viewModel = TransferConfirmViewModel(
            repository = FakeTransferRepository(),
            clientRepo = FakeClientRepository(),
            userVerificationRepository = FakeUserVerificationRepository(),
            savedStateHandle = savedStateHandle,
        )
        advanceUntilIdle()

        viewModel.trySendAction(TransferConfirmAction.AmountChanged("-1"))
        viewModel.trySendAction(TransferConfirmAction.DescriptionChanged("test transfer"))
        viewModel.trySendAction(TransferConfirmAction.InitiateTransfer)
        advanceUntilIdle()

        assertFalse(viewModel.stateFlow.value.isProcessing)
        assertIs<TransferConfirmState.DialogState.Error.ValidationError>(
            viewModel.stateFlow.value.dialogState,
        )
    }

    private inner class FakeTransferRepository : ThirdPartyTransferRepository {
        override suspend fun getTransferTemplate(): AccountOptionsTemplate = AccountOptionsTemplate()

        override fun makeTransfer(payload: TransferPayload): Flow<DataState<TPTResponse>> = error("unused")
    }

    private inner class FakeClientRepository : ClientRepository {
        override fun getClientInfo(clientId: Long): Flow<DataState<Client>> = error("unused")

        override suspend fun getClients(): Flow<DataState<Page<Client>>> = error("unused")

        override suspend fun getClient(clientId: Long): DataState<Client> = error("unused")

        override suspend fun updateClient(clientId: Long, client: UpdatedClient): DataState<String> = error("unused")

        override fun getClientImage(clientId: Long): Flow<DataState<String>> = error("unused")

        override suspend fun updateClientImage(clientId: Long, image: String): DataState<String> = error("unused")

        override suspend fun getClientAccounts(clientId: Long): ClientAccountsEntity = error("unused")

        override suspend fun getAccounts(clientId: Long, accountType: String): Flow<DataState<List<Account>>> =
            error("unused")

        override suspend fun createClient(newClient: NewClient): DataState<Int> = error("unused")

        override suspend fun deleteClient(clientId: Int): DataState<Int> = error("unused")
    }

    private class FakeUserVerificationRepository : UserVerificationRepository {
        override fun recordVerification() = Unit

        override fun consumeVerification(): Boolean = false
    }

    private val savedStateHandle: SavedStateHandle
        get() = SavedStateHandle(
            mapOf(
                "amount" to 0,
                "accountId" to 2L,
                "toOfficeId" to 2,
                "toClientId" to 2L,
                "toAccountTypeId" to 2,
                "toAccountName" to "Receiver",
                "toAccountNo" to "0002",
                "returnDestination" to "home",
            ),
        )
}
