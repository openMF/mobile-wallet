package org.mifos.mobilewallet.mifospay.common.presenter

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.SearchClient
import org.mifos.mobilewallet.mifospay.base.BaseView
import org.mifos.mobilewallet.mifospay.common.SearchContract
import javax.inject.Inject

/**
 * Created by naman on 21/8/17.
 */
class SearchPresenter @Inject constructor(private val mUsecaseHandler: UseCaseHandler) :
    SearchContract.SearchPresenter {
    @JvmField
    @Inject
    var searchClient: SearchClient? = null
    private var mSearchView: SearchContract.SearchView? = null
    override fun attachView(baseView: BaseView<*>?) {
        mSearchView = baseView as SearchContract.SearchView?
        mSearchView?.setPresenter(this)
    }

    override fun performSearch(query: String?) {
        mUsecaseHandler.execute(searchClient, SearchClient.RequestValues(query),
            object : UseCaseCallback<SearchClient.ResponseValue?> {
                override fun onSuccess(response: SearchClient.ResponseValue?) {
                    mSearchView?.showSearchResult(response?.results)
                }

                override fun onError(message: String) {}
            })
    }
}