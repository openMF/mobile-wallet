package mifos.org.mobilewallet.core.data.fineract.entity.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import mifos.org.mobilewallet.core.data.fineract.entity.SearchedEntity;
import mifos.org.mobilewallet.core.domain.model.SearchResult;

/**
 * Created by naman on 19/8/17.
 */

@Singleton
public class SearchedEntitiesMapper {

    @Inject
    SearchedEntitiesMapper() {}

    public List<SearchResult> transformList(List<SearchedEntity> searchedEntities) {
        List<SearchResult> searchResults = new ArrayList<>();

        if (searchedEntities != null && searchedEntities.size() != 0) {
             for (SearchedEntity entity : searchedEntities) {
                 searchResults.add(transform(entity));
             }
        }

        return searchResults;
    }

    public SearchResult transform(SearchedEntity searchedEntity) {

        SearchResult searchResult = new SearchResult();

        searchResult.setResultId(searchedEntity.getEntityId());
        searchResult.setResultName(searchedEntity.getEntityName());
        searchResult.setResultType(searchedEntity.getEntityType());

        return searchResult;
    }
}
