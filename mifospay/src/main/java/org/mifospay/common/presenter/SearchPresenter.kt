/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common.presenter

import org.mifospay.base.BaseView
import org.mifospay.common.SearchContract
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.SearchClient
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
        mUsecaseHandler.execute(
            searchClient,
            SearchClient.RequestValues(query),
            object : UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    mSearchView.showSearchResult(response?.results?.toMutableList())
                }

                override fun onError(message: String) {}
            },
        )
    }
}
