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