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

import io.ktor.http.content.PartData
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.Result
import org.mifospay.core.model.entity.noncore.Document

interface DocumentRepository {
    fun getDocuments(entityType: String, entityId: Int): Flow<Result<List<Document>>>

    fun createDocument(
        entityType: String,
        entityId: Int,
        name: String,
        description: String,
        fileName: PartData.FileItem,
    ): Flow<Result<Unit>>

    fun downloadDocument(entityType: String, entityId: Int, documentId: Int): Flow<Result<Document>>

    fun deleteDocument(entityType: String, entityId: Int, documentId: Int): Flow<Result<Unit>>

    fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        name: String,
        description: String,
        fileName: PartData.FileItem,
    ): Flow<Result<Unit>>
}
