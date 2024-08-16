/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.domain.SearchResult
import com.mifospay.core.model.entity.SearchedEntity
import javax.inject.Inject

class SearchedEntitiesMapper @Inject internal constructor() {
    fun transformList(searchedEntities: List<SearchedEntity>?): List<SearchResult> {
        val searchResults: MutableList<SearchResult> = ArrayList()

        searchedEntities?.forEach { entity ->
            searchResults.add(transform(entity))
        }

        return searchResults
    }

    fun transform(searchedEntity: SearchedEntity): SearchResult {
        val searchResult = SearchResult()
        searchResult.resultId = searchedEntity.entityId
        searchResult.resultName = searchedEntity.entityName
        searchResult.resultType = searchedEntity.entityType
        return searchResult
    }
}
