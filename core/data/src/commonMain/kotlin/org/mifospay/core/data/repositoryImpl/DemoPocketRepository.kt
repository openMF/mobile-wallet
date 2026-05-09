/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.model.pocket.Pocket
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.model.pocket.PocketAccountType

class DemoPocketRepository : PocketRepository {
    private val pocket = MutableStateFlow(
        Pocket(
            id = 1L,
            clientId = 1L,
            clientName = "Demo Client",
            linkedAccounts = listOf(
                PocketAccount(
                    accountId = 1L,
                    accountNumber = "000000001",
                    accountType = PocketAccountType.SAVINGS,
                    balance = 1250.75,
                    currency = "USD",
                    productName = "Voluntary Savings",
                    status = "Active",
                ),
                PocketAccount(
                    accountId = 2L,
                    accountNumber = "000000002",
                    accountType = PocketAccountType.LOAN,
                    balance = -420.00,
                    currency = "USD",
                    productName = "Emergency Loan",
                    status = "Active",
                ),
            ),
        ),
    )

    override fun getPocket(): Flow<DataState<Pocket>> =
        pocket
            .map<Pocket, DataState<Pocket>> { DataState.Success(it) }
            .onStart { emit(DataState.Loading) }

    override fun linkAccount(accountId: Long, accountType: String): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        val type = accountType.toPocketAccountType()
        pocket.update { current ->
            if (current.linkedAccounts.any { it.accountId == accountId }) {
                current
            } else {
                current.copy(
                    linkedAccounts = current.linkedAccounts + accountId.toDemoAccount(type),
                )
            }
        }
        emit(DataState.Success(Unit))
    }

    override fun delinkAccount(accountId: Long): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        pocket.update { current ->
            current.copy(
                linkedAccounts = current.linkedAccounts.filterNot { it.accountId == accountId },
            )
        }
        emit(DataState.Success(Unit))
    }
}

private fun String.toPocketAccountType(): PocketAccountType =
    PocketAccountType.entries.firstOrNull { it.name == uppercase() } ?: PocketAccountType.SAVINGS

private fun Long.toDemoAccount(accountType: PocketAccountType): PocketAccount {
    val productName = when (accountType) {
        PocketAccountType.SAVINGS -> "Savings Account"
        PocketAccountType.LOAN -> "Loan Account"
        PocketAccountType.SHARE -> "Share Account"
    }

    val balance = when (accountType) {
        PocketAccountType.SAVINGS -> 500.00 + this
        PocketAccountType.LOAN -> -250.00 - this
        PocketAccountType.SHARE -> 100.00 + this
    }

    return PocketAccount(
        accountId = this,
        accountNumber = this.toString().padStart(9, '0'),
        accountType = accountType,
        balance = balance,
        currency = "USD",
        productName = productName,
        status = "Active",
    )
}
