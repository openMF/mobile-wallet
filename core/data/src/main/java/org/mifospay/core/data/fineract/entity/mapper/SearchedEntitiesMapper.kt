package org.mifospay.core.data.fineract.entity.mapper

import com.mifospay.core.model.domain.SearchResult
import com.mifospay.core.model.entity.SearchedEntity
import javax.inject.Inject

/**
 * Created by naman on 19/8/17.
 */
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
