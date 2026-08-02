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
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.model.standinginstruction.SITemplate
import org.mifospay.core.model.standinginstruction.SIUpdatePayload
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.model.standinginstruction.StandingInstructionPayload

interface StandingInstructionRepository {
    // Phase-3 cutover — reads on ScreenState.
    fun getStandingInstructionTemplate(
        fromOfficeId: Long,
        fromClientId: Long,
        fromAccountType: Long,
    ): Flow<ScreenState<SITemplate>>

    fun getAllStandingInstructions(
        clientId: Long,
    ): Flow<ScreenState<List<StandingInstruction>>>

    /**
     * Phase-5 Batch-3 LEDGER read — GOAL D13 (`createStore` + CACHE_FIRST_SWR).
     *
     * Store-backed alternative to [getAllStandingInstructions] — reads through
     * the `AppStoreRegistry.StandingInstruction` Store5 (offline-first via
     * `wallet_standing_instructions` Room SoT, SWR revalidation once the TTL
     * elapses).
     *
     * @param clientId the store's page key AND the API query parameter.
     * @param scope the caller's [CoroutineScope] (typically `viewModelScope`)
     *   — Store5 subscribes its internal refresh trigger to this scope.
     */
    fun getAllStandingInstructionsScreen(
        clientId: Long,
        scope: CoroutineScope,
    ): Flow<ScreenState<List<StandingInstruction>>>

    fun getStandingInstruction(instructionId: Long): Flow<ScreenState<StandingInstruction>>

    // Writes stay on DataState (Phase-3 D1).
    suspend fun createStandingInstruction(
        payload: StandingInstructionPayload,
    ): DataState<String>

    suspend fun updateStandingInstruction(
        instructionId: Long,
        payload: SIUpdatePayload,
    ): DataState<String>

    suspend fun deleteStandingInstruction(instructionId: Long): DataState<String>
}
