/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.mifospay.core.common.DataState
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.datastore.PocketPreferencesDataSource
import org.mifospay.core.datastore.model.toEntity
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.network.PocketDataManager
import org.mifospay.core.network.model.ClientResponseEntity
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.client.ClientAccountsEntity
import org.mifospay.core.network.model.entity.client.ClientEntity
import org.mifospay.core.network.model.entity.client.NewClientEntity
import org.mifospay.core.network.model.entity.client.UpdateClientEntity
import org.mifospay.core.network.model.entity.common.CurrencyResponseDto
import org.mifospay.core.network.model.entity.loanAccount.LoanAccountResponseDto
import org.mifospay.core.network.model.entity.loanAccount.LoanStatusResponseDto
import org.mifospay.core.network.model.entity.pocket.PocketAccountDto
import org.mifospay.core.network.model.entity.pocket.PocketCommandResponse
import org.mifospay.core.network.model.entity.pocket.PocketDelinkRequest
import org.mifospay.core.network.model.entity.pocket.PocketLinkRequest
import org.mifospay.core.network.model.entity.pocket.PocketResponseDto
import org.mifospay.core.network.model.entity.shareAccount.ShareStatusResponseDto
import org.mifospay.core.network.model.entity.shareAccount.ShareSummaryResponseDto
import org.mifospay.core.network.model.entity.shareAccount.ShareWithAssociationsResponseDto
import org.mifospay.core.network.services.ClientService
import org.mifospay.core.network.services.PocketService
import org.mifospay.core.network.services.ShareAccountService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PocketRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dataManager: FakePocketDataManager
    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var pocketPreferencesDataSource: PocketPreferencesDataSource
    private lateinit var repository: PocketRepositoryImp

    @BeforeTest
    fun setUp() {
        dataManager = FakePocketDataManager()
        networkMonitor = FakeNetworkMonitor()
        pocketPreferencesDataSource = PocketPreferencesDataSource(
            settings = MapSettings(),
            dispatcher = testDispatcher,
        )
        repository = PocketRepositoryImp(
            dataManager = dataManager,
            networkMonitor = networkMonitor,
            pocketPreferencesDataSource = pocketPreferencesDataSource,
            ioDispatcher = testDispatcher,
        )
    }

    @Test
    fun getPocketAccounts_mapsAllPocketTypesAndNullableFields() = runTest(testDispatcher) {
        dataManager.pocketService.response = PocketResponseDto(
            loanAccounts = listOf(
                PocketAccountDto(
                    pocketId = 1L,
                    accountId = 10L,
                    accountNumber = "LN-10",
                    id = 100L,
                ),
            ),
            savingsAccounts = listOf(
                PocketAccountDto(
                    pocketId = 2L,
                    accountId = null,
                    accountNumber = null,
                    id = null,
                ),
            ),
            shareAccounts = listOf(
                PocketAccountDto(
                    pocketId = 3L,
                    accountId = 30L,
                    accountNumber = "SH-30",
                    id = 300L,
                ),
            ),
        )

        val result = repository.getPocketAccounts()

        val accounts = assertIs<DataState.Success<List<org.mifospay.core.model.pocket.PocketAccount>>>(result).data
        assertEquals(3, accounts.size)
        assertEquals(AccountType.LOAN, accounts[0].accountType)
        assertEquals(10L, accounts[0].accountId)
        assertEquals("LN-10", accounts[0].accountNumber)
        assertEquals(AccountType.SAVINGS, accounts[1].accountType)
        assertEquals(0L, accounts[1].accountId)
        assertEquals("", accounts[1].accountNumber)
        assertEquals(AccountType.SHARE, accounts[2].accountType)
    }

    @Test
    fun getPocketAccounts_returnsCachedAccountsWhenNetworkCallFails() = runTest(testDispatcher) {
        val cachedAccount = PocketAccount(
            pocketId = 1L,
            id = 100L,
            accountId = 10L,
            accountType = AccountType.LOAN,
            accountNumber = "LN-10",
        )
        pocketPreferencesDataSource.updatePocketAccounts(listOf(cachedAccount.toEntity()))
        dataManager.pocketService.failure = IllegalStateException("server unavailable")

        val result = repository.getPocketAccounts()

        val success = assertIs<DataState.Success<List<PocketAccount>>>(result)
        assertEquals(listOf(cachedAccount), success.data)
    }

    @Test
    fun getDetailedPocketAccounts_enrichesPocketWithClientAccountDetails() = runTest(testDispatcher) {
        dataManager.pocketService.response = PocketResponseDto(
            loanAccounts = listOf(pocketDto(accountId = 10L, type = AccountType.LOAN)),
        )
        dataManager.clientService.accounts = ClientAccountsEntity(
            loanAccounts = listOf(
                loanAccount(
                    id = 10L,
                    productName = "Personal loan",
                    balance = 1250.5,
                    status = LoanStatusResponseDto(active = true),
                ),
            ),
        )

        val result = repository.getDetailedPocketAccounts(clientId = 42L).first { it is DataState.Success }

        val account = assertIs<DataState.Success<List<DetailedPocketAccount>>>(result).data.single()
        assertEquals(10L, account.pocket.accountId)
        assertEquals("Personal loan", account.productName)
        assertEquals(1250.5, account.balance)
        assertEquals("USD", account.currencyCode)
        assertEquals("$", account.currencyDisplaySymbol)
        assertEquals(2, account.decimalPlaces)
        assertEquals(org.mifospay.core.model.pocket.AccountStatus.ACTIVE, account.status)
    }

    @Test
    fun getAvailableAccountsToLink_excludesAccountsAlreadyInPocketAndMapsRemainingAccounts() =
        runTest(testDispatcher) {
            dataManager.pocketService.response = PocketResponseDto(
                loanAccounts = listOf(pocketDto(accountId = 10L, type = AccountType.LOAN)),
            )
            dataManager.clientService.accounts = ClientAccountsEntity(
                loanAccounts = listOf(
                    loanAccount(id = 10L, productName = "Already linked"),
                    loanAccount(id = 11L, productName = "Available loan", balance = 90.0),
                ),
            )
            repository.getDetailedPocketAccounts(clientId = 42L).first { it is DataState.Success }

            val result = repository.getAvailableAccountsToLink(clientId = 42L).first { it is DataState.Success }

            val accounts = assertIs<DataState.Success<List<org.mifospay.core.model.pocket.LinkableAccount>>>(result).data
            assertEquals(1, accounts.size)
            assertEquals(11L, accounts.single().accountId)
            assertEquals("Available loan", accounts.single().productName)
            assertEquals(90.0, accounts.single().balance)
            assertEquals(AccountType.LOAN, accounts.single().accountType)
        }

    @Test
    fun linkAccounts_mapsPayloadAndRefreshesDetailedCache() = runTest(testDispatcher) {
        dataManager.pocketService.response = PocketResponseDto(
            loanAccounts = listOf(pocketDto(accountId = 11L, type = AccountType.LOAN)),
        )
        dataManager.clientService.accounts = ClientAccountsEntity(
            loanAccounts = listOf(loanAccount(id = 11L, productName = "New loan")),
        )

        val payload = PocketLinkPayload(
            accountsDetail = listOf(
                PocketLinkPayload.AccountDetail(
                    accountId = "11",
                    accountType = AccountType.LOAN,
                ),
            ),
        )

        val result = repository.linkAccounts(
            payload = payload,
            explicitlyAddedAccounts = emptyList(),
            clientId = 42L,
        )

        assertIs<DataState.Success<Unit>>(result)
        assertEquals("linkAccounts", dataManager.pocketService.lastLinkCommand)
        assertEquals(
            PocketLinkRequest.AccountDetail(accountId = "11", accountType = "LOAN"),
            dataManager.pocketService.lastLinkRequest?.accountsDetail?.single(),
        )

        val cached = repository.getDetailedPocketAccounts(clientId = 42L).first { it is DataState.Success }
        assertEquals("New loan", assertIs<DataState.Success<List<DetailedPocketAccount>>>(cached).data.single().productName)
    }

    @Test
    fun delinkAccounts_sendsPositiveMappingIdsAndRemovesAccountFromCache() = runTest(testDispatcher) {
        dataManager.pocketService.response = PocketResponseDto(
            loanAccounts = listOf(pocketDto(accountId = 10L, type = AccountType.LOAN)),
        )
        dataManager.clientService.accounts = ClientAccountsEntity(
            loanAccounts = listOf(loanAccount(id = 10L)),
        )
        repository.getDetailedPocketAccounts(clientId = 42L).first { it is DataState.Success }

        val result = repository.delinkAccounts(
            pocketAccountMappingIds = listOf(100L, -1L),
            clientId = 42L,
        )

        assertIs<DataState.Success<Unit>>(result)
        assertEquals(PocketDelinkRequest(listOf(100L)), dataManager.pocketService.lastDelinkRequest)
        val cached = repository.getDetailedPocketAccounts(clientId = 42L).first { it is DataState.Success }
        assertTrue(assertIs<DataState.Success<List<DetailedPocketAccount>>>(cached).data.isEmpty())
    }

    private fun pocketDto(accountId: Long, type: AccountType) = PocketAccountDto(
        pocketId = accountId + 1,
        accountId = accountId,
        accountNumber = "ACC-$accountId",
        id = accountId + 90,
        accountType = when (type) {
            AccountType.LOAN -> 1
            AccountType.SAVINGS -> 2
            AccountType.SHARE -> 3
        },
    )

    private fun loanAccount(
        id: Long,
        productName: String = "Loan",
        balance: Double = 0.0,
        status: LoanStatusResponseDto? = null,
    ) = LoanAccountResponseDto(
        id = id,
        accountNo = "LOAN-$id",
        productName = productName,
        loanBalance = balance,
        status = status,
        currency = CurrencyResponseDto(
            code = "USD",
            decimalPlaces = 2,
            displaySymbol = "$",
        ),
    )
}

private class FakeNetworkMonitor : NetworkMonitor {
    var online: Boolean = true
    override val isOnline: Flow<Boolean>
        get() = flowOf(online)
}

private class FakePocketDataManager : PocketDataManager {
    val clientService = FakeClientService()
    val pocketService = FakePocketService()
    val shareAccountService = FakeShareAccountService()

    override val clientsApi: ClientService get() = clientService
    override val pocketApi: PocketService get() = pocketService
    override val shareAccountApi: ShareAccountService get() = shareAccountService
}

private class FakePocketService : PocketService {
    var response: PocketResponseDto = PocketResponseDto()
    var failure: Exception? = null
    var lastLinkCommand: String? = null
    var lastLinkRequest: PocketLinkRequest? = null
    var lastDelinkRequest: PocketDelinkRequest? = null

    override suspend fun getPocketAccounts(): PocketResponseDto {
        failure?.let { throw it }
        return response
    }

    override suspend fun linkAccounts(command: String, request: PocketLinkRequest): PocketCommandResponse {
        lastLinkCommand = command
        lastLinkRequest = request
        return PocketCommandResponse(resourceId = 1L)
    }

    override suspend fun delinkAccounts(command: String, request: PocketDelinkRequest): PocketCommandResponse {
        lastDelinkRequest = request
        return PocketCommandResponse(resourceId = 1L)
    }
}

private class FakeShareAccountService : ShareAccountService {
    var details = ShareWithAssociationsResponseDto(
        productName = "Shares",
        status = ShareStatusResponseDto(active = true),
        summary = ShareSummaryResponseDto(totalApprovedShares = 1),
        currentMarketPrice = 1.0,
    )

    override fun getShareAccountDetails(
        accountId: Long,
        associations: String,
    ): Flow<ShareWithAssociationsResponseDto> = flowOf(details)
}

private class FakeClientService : ClientService {
    var accounts = ClientAccountsEntity()

    override suspend fun clients(): Flow<Page<ClientEntity>> = error("Not used by pocket tests")
    override suspend fun getClientForId(clientId: Long): ClientEntity = error("Not used by pocket tests")
    override fun getClient(clientId: Long): Flow<ClientEntity> = error("Not used by pocket tests")
    override suspend fun updateClient(clientId: Long, payload: UpdateClientEntity) = error("Not used by pocket tests")
    override fun getClientImage(clientId: Long): Flow<String> = error("Not used by pocket tests")
    override suspend fun updateClientImage(clientId: Long, typedFile: String) = error("Not used by pocket tests")
    override suspend fun getClientAccounts(clientId: Long): ClientAccountsEntity = accounts
    override fun getAccounts(clientId: Long, accountType: String): Flow<ClientAccountsEntity> = error("Not used by pocket tests")
    override suspend fun createClient(newClient: NewClientEntity): ClientResponseEntity = error("Not used by pocket tests")
    override suspend fun deleteClient(clientId: Int): ClientResponseEntity = error("Not used by pocket tests")
}
