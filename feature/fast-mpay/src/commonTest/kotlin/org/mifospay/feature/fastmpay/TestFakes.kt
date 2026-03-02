/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.fastmpay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.data.repository.OfficeRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.UpdatedClient
import org.mifospay.core.model.instance.InterbankServer
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.model.office.Office
import org.mifospay.core.model.user.UserInfo
import org.mifospay.core.network.model.entity.templates.beneficiary.BeneficiaryTemplate

/**
 * Fake implementation of [OfficeRepository] for testing.
 */
internal class FakeOfficeRepository : OfficeRepository {
    private var officeList: List<Office> = listOf(
        Office(id = 1, name = "Head Office"),
        Office(id = 5, name = "Lagos Branch"),
    )
    private var shouldReturnError = false

    fun setOfficeList(list: List<Office>) {
        officeList = list
    }

    fun setShouldReturnError(error: Boolean) {
        shouldReturnError = error
    }

    override fun getOffices(): Flow<DataState<List<Office>>> {
        return if (shouldReturnError) {
            flowOf(DataState.Error(Throwable("Network error")))
        } else {
            flowOf(DataState.Success(officeList))
        }
    }
}

/**
 * Fake implementation of [BeneficiaryRepository] for testing.
 */
internal class FakeBeneficiaryRepository : BeneficiaryRepository {
    private var beneficiaryList: List<Beneficiary> = emptyList()
    private var shouldReturnError = false

    fun setBeneficiaryList(list: List<Beneficiary>) {
        beneficiaryList = list
    }

    fun setShouldReturnError(error: Boolean) {
        shouldReturnError = error
    }

    override suspend fun getBeneficiaryList(): Flow<DataState<List<Beneficiary>>> {
        return if (shouldReturnError) {
            flowOf(DataState.Error(Throwable("Network error")))
        } else {
            flowOf(DataState.Success(beneficiaryList))
        }
    }

    override suspend fun getBeneficiaryTemplate(): Flow<DataState<BeneficiaryTemplate>> {
        return flowOf(DataState.Success(BeneficiaryTemplate()))
    }

    override suspend fun createBeneficiary(
        beneficiaryPayload: BeneficiaryPayload,
    ): DataState<String> {
        return DataState.Success("Success")
    }

    override suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): DataState<String> {
        return DataState.Success("Success")
    }

    override suspend fun deleteBeneficiary(beneficiaryId: Long): DataState<String> {
        return DataState.Success("Success")
    }
}

/**
 * Fake implementation of [UserPreferencesRepository] for testing.
 */
internal class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val _selectedInstance = MutableStateFlow<ServerInstance?>(null)

    override val selectedInstance: StateFlow<ServerInstance?> = _selectedInstance

    fun setSelectedInstance(instance: ServerInstance?) {
        _selectedInstance.value = instance
    }

    override val userInfo: Flow<UserInfo> = flowOf(
        UserInfo(
            username = "",
            userId = 0L,
            base64EncodedAuthenticationKey = "",
            authenticated = false,
            officeId = 0,
            officeName = "",
            roles = emptyList(),
            permissions = emptyList(),
            clients = emptyList(),
            shouldRenewPassword = false,
            isTwoFactorAuthenticationRequired = false,
        ),
    )
    override val token: StateFlow<String?> = MutableStateFlow(null)
    override val client: StateFlow<Client?> = MutableStateFlow(null)
    override val clientId: StateFlow<Long?> = MutableStateFlow(null)
    override val authToken: String? = null
    override val defaultAccount: StateFlow<DefaultAccount?> = MutableStateFlow(null)
    override val defaultAccountId: StateFlow<Long?> = MutableStateFlow(null)
    override val selectedInterbankInstance: StateFlow<InterbankServer?> = MutableStateFlow(null)
    override val accountExternalIds: StateFlow<Map<Long, String>> = MutableStateFlow(emptyMap())

    override suspend fun updateToken(token: String): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateUserInfo(user: UserInfo): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateClientInfo(client: Client): DataState<Unit> = DataState.Success(Unit)
    override suspend fun updateClientProfile(client: UpdatedClient): DataState<Unit> =
        DataState.Success(Unit)

    override suspend fun updateDefaultAccount(account: DefaultAccount): DataState<Unit> =
        DataState.Success(Unit)

    override suspend fun updateSelectedInstance(instance: ServerInstance): DataState<Unit> =
        DataState.Success(Unit)

    override suspend fun updateSelectedInterbankInstance(instance: InterbankServer): DataState<Unit> =
        DataState.Success(Unit)

    override suspend fun updateAccountExternalIds(
        accountExternalIds: Map<Long, String>,
    ): DataState<Unit> = DataState.Success(Unit)

    override fun getAccountExternalId(accountId: Long): String? = null
    override suspend fun logOut() {}
}
