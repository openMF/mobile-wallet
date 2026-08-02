/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.CoroutineScope
import kpt.core.base.store.screen.ScreenDataStream
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.model.savingsaccount.CreateNewSavingEntity
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.model.savingsaccount.SavingAccountTemplate
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.UpdateSavingAccountEntity
import org.mifospay.core.network.model.entity.Page

interface SavingsAccountRepository {
    // Phase-3 cutover — Flow-shaped reads on ScreenState.
    // Page<T> is preserved inside Content (mirrors
    // `SelfServiceRepository.getSelfClientDetails(): Page<Client>` treatment).
    suspend fun getSavingsAccounts(limit: Int): ScreenStateStream<Page<SavingsWithAssociationsEntity>>

    suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): ScreenStateStream<SavingsWithAssociationsEntity>

    fun getAccountDetail(accountId: Long): ScreenStateStream<SavingAccountDetail>

    /**
     * Phase-5 Batch-1 **SINGLE-ROW-PER-KEY read** for the `accountDetail` archetype
     * (GOAL D13) — returns an offline-first `ScreenStateStream<SavingAccountDetail>`
     * consumed through the `accountDetail` Store5
     * [`Store`][org.mobilenativefoundation.store.store5.Store]
     * (`createStore` + Room
     * [`SourceOfTruth`][org.mobilenativefoundation.store.store5.SourceOfTruth]
     * + single-row upsert writer) via
     * [`Store.asScreenStream`][kpt.core.base.store.screen.asScreenStream].
     *
     * The store is driven by
     * [`FetchPolicy.CACHE_FIRST_SWR`][kpt.core.base.store.screen.FetchPolicy.CACHE_FIRST_SWR]
     * (Phase-5 T10 default). Distinct from the transitional
     * [`asScreenStateFlow`][org.mifospay.core.common.asScreenStateFlow] the
     * legacy [getAccountDetail] path uses; both signatures return
     * `ScreenStateStream<SavingAccountDetail>` so a consumer can swap between
     * them without any downstream shape change.
     *
     * ### Write path (GOAL D1)
     *
     * This is a READ path. Account management writes ([blockAccount],
     * [unblockAccount], [createSavingsAccount], [updateSavingsAccount]) continue
     * to flow through their existing online paths and appear here on the next
     * refresh cycle.
     *
     * @param accountId Fineract savings-account id (SoT key).
     * @param scope Coroutine scope for the stream's internal helper coroutines
     *   (typically `viewModelScope`).
     */
    fun getAccountDetailStream(
        accountId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<SavingAccountDetail>

    suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): ScreenStateStream<Transaction>

    suspend fun payViaMobile(accountId: Long): ScreenStateStream<Transaction>

    fun getSavingAccountTemplate(clientId: Long): ScreenStateStream<SavingAccountTemplate>

    // Writes stay on DataState (Phase-3 D1).
    suspend fun createSavingsAccount(savingAccount: CreateNewSavingEntity): DataState<String>

    suspend fun updateSavingsAccount(
        accountId: Long,
        savingAccount: UpdateSavingAccountEntity,
    ): DataState<String>

    suspend fun unblockAccount(
        accountId: Long,
    ): DataState<String>

    suspend fun blockAccount(
        accountId: Long,
    ): DataState<String>
}
