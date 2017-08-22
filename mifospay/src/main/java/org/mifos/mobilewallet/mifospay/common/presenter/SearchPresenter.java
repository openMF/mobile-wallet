package org.mifos.mobilewallet.mifospay.common.presenter;

import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.common.SearchContract;

import javax.inject.Inject;

import mifos.org.mobilewallet.core.base.UseCase;
import mifos.org.mobilewallet.core.base.UseCaseHandler;
import mifos.org.mobilewallet.core.domain.usecase.SearchClient;

/**
 * Created by naman on 21/8/17.
 */

public class SearchPresenter implements SearchContract.SearchPresenter {

    private SearchContract.SearchView mSearchView;
    private final UseCaseHandler mUsecaseHandler;

    @Inject
    SearchClient searchClient;

    @Inject
    public SearchPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    @Override
    public void attachView(BaseView baseView) {
        mSearchView = (SearchContract.SearchView) baseView;
        mSearchView.setPresenter(this);
    }

    @Override
    public void performSearch(String query) {
        mUsecaseHandler.execute(searchClient, new SearchClient.RequestValues(query),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
            @Override
            public void onSuccess(SearchClient.ResponseValue response) {
                mSearchView.showSearchResult(response.getResults());
            }

            @Override
            public void onError(String message) {

            }
        });
    }
}
