/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.viewmodels

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mifospay.core.common.DataState
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.PocketAccount
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PocketDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferences: FakeUserPreferencesRepository
    private lateinit var repository: FakePocketRepository
    private lateinit var stringProvider: FakeStringProvider
    private lateinit var viewModel: PocketDashboardViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferences = FakeUserPreferencesRepository()
        repository = FakePocketRepository()
        stringProvider = FakeStringProvider()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun givenEmptyRepository_whenViewModelLoads_thenEmptyStateIsPublished() = runTest(testDispatcher) {
        repository.setDetailedPocketAccounts(DataState.Success(emptyList()))
        createViewModel()

        advanceUntilIdle()

        assertIs<PocketDashboardUiState.Empty>(viewModel.stateFlow.value.uiState)
        assertEquals(1L, viewModel.stateFlow.value.clientId)
        assertTrue(repository.resetPocketCacheCalled)
    }

    @Test
    fun givenRepositoryLoading_whenViewModelLoads_thenLoadingStateIsPublished() = runTest(testDispatcher) {
        repository.setDetailedPocketAccounts(DataState.Loading)
        createViewModel()

        advanceUntilIdle()

        assertIs<PocketDashboardUiState.Loading>(viewModel.stateFlow.value.uiState)
    }

    @Test
    fun givenRepositoryError_whenViewModelLoads_thenErrorStateIsPublished() = runTest(testDispatcher) {
        repository.setDetailedPocketAccounts(DataState.Error(Exception("network")))
        createViewModel()

        advanceUntilIdle()

        assertIs<PocketDashboardUiState.Error>(viewModel.stateFlow.value.uiState)
        assertFalse(viewModel.stateFlow.value.isRefreshing)
    }

    @Test
    fun givenAccountsOfEachType_whenViewModelLoads_thenAccountsAreCategorizedAndTotalIsFormatted() =
        runTest(testDispatcher) {
            repository.setDetailedPocketAccounts(
                DataState.Success(
                    listOf(
                        detailedAccount(101L, AccountType.SAVINGS, "Savings", 100.0),
                        detailedAccount(102L, AccountType.LOAN, "Loan", 50.0),
                        detailedAccount(103L, AccountType.SHARE, "Shares", 25.0),
                        detailedAccount(
                            accountId = 104L,
                            type = AccountType.SAVINGS,
                            productName = null,
                            balance = null,
                            status = AccountStatus.PENDING,
                        ),
                    ),
                ),
            )
            createViewModel()

            advanceUntilIdle()

            val state = viewModel.stateFlow.value
            assertIs<PocketDashboardUiState.Success>(state.uiState)
            assertEquals(listOf(101L, 104L), state.savingsAccounts.map { it.accountId })
            assertEquals(listOf(102L), state.loanAccounts.map { it.accountId })
            assertEquals(listOf(103L), state.shareAccounts.map { it.accountId })
            assertEquals("USD $175.00", state.totalBalance)
            assertEquals("PENDING", state.savingsAccounts[1].balanceOrStatus)
            assertEquals("Unknown Account", state.savingsAccounts[1].name)
        }

    @Test
    fun whenRetryIsClicked_thenForceRefreshLoadsTheLatestState() = runTest(testDispatcher) {
        repository.setDetailedPocketAccounts(DataState.Error(Exception("first attempt")))
        createViewModel()
        advanceUntilIdle()

        repository.setDetailedPocketAccounts(DataState.Success(emptyList()))
        viewModel.trySendAction(PocketDashboardAction.Retry)
        advanceUntilIdle()

        assertIs<PocketDashboardUiState.Empty>(viewModel.stateFlow.value.uiState)
        assertTrue(repository.lastDetailedForceRefresh == true)
    }

    @Test
    fun givenMultipleCurrencies_whenViewModelLoads_thenTotalIsFormattedPerCurrency() =
        runTest(testDispatcher) {
            repository.setDetailedPocketAccounts(
                DataState.Success(
                    listOf(
                        detailedAccount(
                            accountId = 101L,
                            type = AccountType.SAVINGS,
                            productName = "US savings",
                            balance = 100.0,
                        ),
                        detailedAccount(
                            accountId = 102L,
                            type = AccountType.LOAN,
                            productName = "Euro loan",
                            balance = 50.0,
                        ).copy(
                            currencyCode = "EUR",
                            currencyDisplaySymbol = "€",
                        ),
                    ),
                ),
            )
            createViewModel()

            advanceUntilIdle()

            assertEquals("USD $100.00\nEUR €50.00", viewModel.stateFlow.value.totalBalance)
        }

    @Test
    fun whenRefreshIsClicked_thenRefreshingStateIsClearedAfterSuccess() = runTest(testDispatcher) {
        repository.setDetailedPocketAccounts(DataState.Success(emptyList()))
        createViewModel()
        advanceUntilIdle()

        repository.setDetailedPocketAccounts(DataState.Loading)
        viewModel.trySendAction(PocketDashboardAction.Refresh)
        advanceUntilIdle()
        assertTrue(viewModel.stateFlow.value.isRefreshing)

        repository.setDetailedPocketAccounts(DataState.Success(emptyList()))
        advanceUntilIdle()

        assertFalse(viewModel.stateFlow.value.isRefreshing)
        assertTrue(repository.lastDetailedForceRefresh == true)
    }

    @Test
    fun whenNavigationActionsAreHandled_thenExpectedEventsAreEmitted() = runTest(testDispatcher) {
        createViewModel()

        viewModel.eventFlow.test {
            viewModel.trySendAction(PocketDashboardAction.NavigateBack)
            advanceUntilIdle()
            assertEquals(PocketDashboardEvent.NavigateBack, awaitItem())

            viewModel.trySendAction(PocketDashboardAction.ManagePocket)
            advanceUntilIdle()
            assertEquals(PocketDashboardEvent.ManagePocket, awaitItem())

            viewModel.trySendAction(PocketDashboardAction.LinkFirstAccount)
            advanceUntilIdle()
            assertEquals(PocketDashboardEvent.ManagePocket, awaitItem())

            viewModel.trySendAction(PocketDashboardAction.NavigateToLoanDetail(10L))
            advanceUntilIdle()
            assertEquals(PocketDashboardEvent.NavigateToLoanDetail(10L), awaitItem())

            viewModel.trySendAction(PocketDashboardAction.NavigateToSavingsDetail(11L))
            advanceUntilIdle()
            assertEquals(PocketDashboardEvent.NavigateToSavingsDetail(11L), awaitItem())

            viewModel.trySendAction(PocketDashboardAction.NavigateToShareDetail(12L))
            advanceUntilIdle()
            assertEquals(PocketDashboardEvent.NavigateToShareDetail(12L), awaitItem())
        }
    }

    private fun createViewModel() {
        viewModel = PocketDashboardViewModel(
            pocketRepository = repository,
            userPreferencesRepository = preferences,
            stringProvider = stringProvider,
        )
    }

    private fun detailedAccount(
        accountId: Long,
        type: AccountType,
        productName: String?,
        balance: Double?,
        status: AccountStatus = AccountStatus.ACTIVE,
    ) = DetailedPocketAccount(
        pocket = PocketAccount(
            pocketId = accountId,
            id = accountId,
            accountId = accountId,
            accountType = type,
            accountNumber = "ACC-$accountId",
        ),
        productName = productName,
        balance = balance,
        currencyCode = "USD",
        currencyDisplaySymbol = "$",
        decimalPlaces = 2,
        status = status,
    )
}
