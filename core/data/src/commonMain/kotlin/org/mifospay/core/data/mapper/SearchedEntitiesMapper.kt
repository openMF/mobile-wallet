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

import org.mifospay.core.model.domain.SearchResult
import org.mifospay.core.model.entity.SearchedEntity

fun SearchedEntity.toSearchResult(): SearchResult {
    return SearchResult(
        resultId = this.entityId,
        resultName = this.entityName,
        resultType = this.entityType,
    )
}

fun List<SearchedEntity>.toSearchResult(): List<SearchResult> = map { it.toSearchResult() }
