/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.mapper

import org.mifospay.core.model.search.SearchResult
import org.mifospay.core.network.model.entity.SearchedEntity

fun SearchedEntity.toModel(): SearchResult {
    return SearchResult(
        entityId = entityId,
        entityAccountNo = entityAccountNo,
        entityName = entityName,
        entityType = entityType,
        parentId = parentId,
        parentName = parentName,
    )
}

fun List<SearchedEntity>.toSearchResult(): List<SearchResult> = map { it.toModel() }
