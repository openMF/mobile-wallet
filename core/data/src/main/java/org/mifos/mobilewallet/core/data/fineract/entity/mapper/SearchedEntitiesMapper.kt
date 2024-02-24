package org.mifos.mobilewallet.core.data.fineract.entity.mapper

import com.mifos.mobilewallet.model.domain.SearchResult
import com.mifos.mobilewallet.model.entity.SearchedEntity
import javax.inject.Inject

/**
 * Created by naman on 19/8/17.
 */
class SearchedEntitiesMapper @Inject internal constructor() {
    fun transformList(searchedEntities: List<SearchedEntity>?): List<SearchResult> {
        val searchResults: MutableList<SearchResult> = ArrayList()
        if (searchedEntities != null && searchedEntities.size != 0) {
            for (entity in searchedEntities) {
                searchResults.add(transform(entity))
            }
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
