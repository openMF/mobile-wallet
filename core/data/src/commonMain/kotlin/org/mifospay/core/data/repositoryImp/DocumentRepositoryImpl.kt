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

import io.ktor.http.content.PartData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.DocumentRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.entity.noncore.Document

class DocumentRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : DocumentRepository {
    override suspend fun getDocuments(entityType: String, entityId: Int): Flow<DataState<List<Document>>> {
        return apiManager.documentApi
            .getDocuments(entityType, entityId)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun createDocument(
        entityType: String,
        entityId: Int,
        name: String,
        description: String,
        fileName: PartData.FileItem,
    ): Flow<DataState<Unit>> {
        return apiManager.documentApi
            .createDocument(entityType, entityId, name, description, fileName)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): Flow<DataState<Document>> {
        return apiManager.documentApi
            .downloadDocument(entityType, entityId, documentId)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun deleteDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): Flow<DataState<Unit>> {
        return apiManager.documentApi
            .removeDocument(entityType, entityId, documentId)
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        name: String,
        description: String,
        fileName: PartData.FileItem,
    ): Flow<DataState<Unit>> {
        return apiManager.documentApi
            .updateDocument(entityType, entityId, documentId, name, description, fileName)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }
}
