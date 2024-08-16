/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.common

import com.mifospay.core.model.domain.SearchResult
import org.mifospay.base.BasePresenter
import org.mifospay.base.BaseView

/**
 * Created by naman on 21/8/17.
 */
interface SearchContract {
    interface SearchView : BaseView<SearchPresenter?> {
        fun showSearchResult(searchResults: MutableList<SearchResult>?)
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
    }

    interface SearchPresenter : BasePresenter {
        fun performSearch(query: String)
    }
}
