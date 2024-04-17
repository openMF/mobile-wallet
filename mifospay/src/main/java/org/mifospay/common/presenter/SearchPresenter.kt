package org.mifospay.common.presenter

import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.SearchClient
import org.mifospay.base.BaseView
import org.mifospay.common.SearchContract
import javax.inject.Inject

/**
 * Created by naman on 21/8/17.
 */
class SearchPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    SearchContract.SearchPresenter {

    @Inject
    lateinit var searchClient: SearchClient
    private lateinit var mSearchView: SearchContract.SearchView
    override fun attachView(baseView: BaseView<*>?) {
        mSearchView = baseView as SearchContract.SearchView
        mSearchView.setPresenter(this)
    }

    override fun performSearch(query: String) {
        mUsecaseHandler.execute(searchClient, SearchClient.RequestValues(query),
            object : UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    mSearchView.showSearchResult(response?.results?.toMutableList())
                }

                override fun onError(message: String) {}
            })
    }
}