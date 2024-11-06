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

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.standinginstruction.SITemplate
import org.mifospay.core.model.standinginstruction.SIUpdatePayload
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.model.standinginstruction.StandingInstructionPayload

interface StandingInstructionRepository {
    fun getStandingInstructionTemplate(
        fromOfficeId: Long,
        fromClientId: Long,
        fromAccountType: Long,
    ): Flow<DataState<SITemplate>>

    fun getAllStandingInstructions(
        clientId: Long,
    ): Flow<DataState<List<StandingInstruction>>>

    fun getStandingInstruction(instructionId: Long): Flow<DataState<StandingInstruction>>

    suspend fun createStandingInstruction(
        payload: StandingInstructionPayload,
    ): DataState<String>

    suspend fun updateStandingInstruction(
        instructionId: Long,
        payload: SIUpdatePayload,
    ): DataState<String>

    suspend fun deleteStandingInstruction(instructionId: Long): DataState<String>
}
