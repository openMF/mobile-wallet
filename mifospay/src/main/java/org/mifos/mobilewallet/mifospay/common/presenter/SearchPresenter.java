package org.mifos.mobilewallet.mifospay.common.presenter;

import org.mifos.mobilewallet.core.base.UseCase;
import org.mifos.mobilewallet.core.base.UseCaseHandler;
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient;
import org.mifos.mobilewallet.mifospay.base.BaseView;
import org.mifos.mobilewallet.mifospay.common.SearchContract;

import javax.inject.Inject;

/**
 * This class is the Presenter component of the common search package.
 * @author naman
 * @since 21/8/17
 */

public class SearchPresenter implements SearchContract.SearchPresenter {

    private final UseCaseHandler mUsecaseHandler;
    @Inject
    SearchClient searchClient;
    private SearchContract.SearchView mSearchView;

    /**
     * Constructor for class SearchPresenter which is used to initialize
     * the objects that are passed as arguments.
     * @param useCaseHandler : An object of UseCaseHandler
     */
    @Inject
    public SearchPresenter(UseCaseHandler useCaseHandler) {
        this.mUsecaseHandler = useCaseHandler;
    }

    /**
     * Attaches View to the Presenter.
     * @param baseView : The view which is set as the SearchView
     */
    @Override
    public void attachView(BaseView baseView) {
        mSearchView = (SearchContract.SearchView) baseView;
        mSearchView.setPresenter(this);
    }

    /**
     * An overridden method from SearchContract to perform Search.
     * @param query : The query to be search
     */
    @Override
    public void performSearch(String query) {
        mUsecaseHandler.execute(searchClient, new SearchClient.RequestValues(query),
                new UseCase.UseCaseCallback<SearchClient.ResponseValue>() {
                    /**
                     * An overridden method called when the task completes successfully.
                     * @param response : The result of the Task
                     */
                    @Override
                    public void onSuccess(SearchClient.ResponseValue response) {
                        mSearchView.showSearchResult(response.getResults());
                    }

                    /**
                     * An overridden method called when the task fails with an exception.
                     * @param message : The exception that caused the task to fail
                     */
                    @Override
                    public void onError(String message) {

                    }
                });
    }
}
