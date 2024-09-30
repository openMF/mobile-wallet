/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.repository.StandingInstructionRepository
import org.mifospay.core.model.entity.Page
import org.mifospay.core.model.entity.payload.StandingInstructionPayload
import org.mifospay.core.model.entity.standinginstruction.SDIResponse
import org.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.GenericResponse

class StandingInstructionRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : StandingInstructionRepository {
    override suspend fun getAllStandingInstructions(
        clientId: Long,
    ): Flow<Result<Page<StandingInstruction>>> {
        return apiManager.standingInstructionApi
            .getAllStandingInstructions(clientId)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun getStandingInstruction(
        instructionId: Long,
    ): Flow<Result<StandingInstruction>> {
        return apiManager.standingInstructionApi
            .getStandingInstruction(instructionId)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun createStandingInstruction(
        payload: StandingInstructionPayload,
    ): Flow<Result<SDIResponse>> {
        return apiManager.standingInstructionApi
            .createStandingInstruction(payload)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun updateStandingInstruction(
        instructionId: Long,
        payload: StandingInstructionPayload,
    ): Flow<Result<GenericResponse>> {
        return apiManager.standingInstructionApi
            .updateStandingInstruction(instructionId, payload, "update")
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun deleteStandingInstruction(
        instructionId: Long,
    ): Flow<Result<GenericResponse>> {
        return apiManager.standingInstructionApi
            .deleteStandingInstruction(instructionId, "delete")
            .asResult().flowOn(ioDispatcher)
    }
}
