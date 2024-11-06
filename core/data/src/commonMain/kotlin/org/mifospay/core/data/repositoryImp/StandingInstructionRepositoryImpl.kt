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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.StandingInstructionRepository
import org.mifospay.core.model.standinginstruction.SITemplate
import org.mifospay.core.model.standinginstruction.SIUpdatePayload
import org.mifospay.core.model.standinginstruction.StandingInstruction
import org.mifospay.core.model.standinginstruction.StandingInstructionPayload
import org.mifospay.core.network.FineractApiManager

class StandingInstructionRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : StandingInstructionRepository {
    override fun getStandingInstructionTemplate(
        fromOfficeId: Long,
        fromClientId: Long,
        fromAccountType: Long,
    ): Flow<DataState<SITemplate>> {
        return apiManager.standingInstructionApi
            .getStandingInstructionTemplate(fromOfficeId, fromClientId, fromAccountType)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getAllStandingInstructions(
        clientId: Long,
    ): Flow<DataState<List<StandingInstruction>>> {
        return apiManager.standingInstructionApi
            .getAllStandingInstructions(clientId)
            .catch { DataState.Error(it, null) }
            .map { it.pageItems }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getStandingInstruction(
        instructionId: Long,
    ): Flow<DataState<StandingInstruction>> {
        return apiManager.standingInstructionApi
            .getStandingInstruction(instructionId)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun createStandingInstruction(
        payload: StandingInstructionPayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.standingInstructionApi.createStandingInstruction(payload)
            }

            DataState.Success("Standing Instruction created successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun updateStandingInstruction(
        instructionId: Long,
        payload: SIUpdatePayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.standingInstructionApi.updateStandingInstruction(
                    instructionId = instructionId,
                    payload = payload,
                    command = "update",
                )
            }

            DataState.Success("Standing Instruction updated successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun deleteStandingInstruction(
        instructionId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.standingInstructionApi.deleteStandingInstruction(
                    instructionId = instructionId,
                    command = "delete",
                )
            }

            DataState.Success("Standing Instruction deleted successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }
}
