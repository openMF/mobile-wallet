package org.mifos.mobilewallet.mifospay.common.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.common.SearchContract;

import javax.inject.Inject;

/**
 * Created by naman on 21/8/17.
 */

public class SearchPresenter implements SearchContract.SearchPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    SearchClient searchClient;
    private SearchContract.SearchView mSearchView;

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
