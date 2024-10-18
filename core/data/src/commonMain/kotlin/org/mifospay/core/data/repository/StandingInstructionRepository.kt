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
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.Page
import org.mifospay.core.network.model.entity.payload.StandingInstructionPayload
import org.mifospay.core.network.model.entity.standinginstruction.SDIResponse
import org.mifospay.core.network.model.entity.standinginstruction.StandingInstruction

interface StandingInstructionRepository {
    suspend fun getAllStandingInstructions(
        clientId: Long,
    ): Flow<DataState<Page<StandingInstruction>>>

    suspend fun getStandingInstruction(instructionId: Long): Flow<DataState<StandingInstruction>>

    suspend fun createStandingInstruction(
        payload: StandingInstructionPayload,
    ): Flow<DataState<SDIResponse>>

    suspend fun updateStandingInstruction(
        instructionId: Long,
        payload: StandingInstructionPayload,
    ): Flow<DataState<GenericResponse>>

    suspend fun deleteStandingInstruction(instructionId: Long): Flow<DataState<GenericResponse>>
}
