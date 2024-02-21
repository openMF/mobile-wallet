package org.mifos.mobilewallet.mifospay.common

import com.mifos.mobilewallet.model.domain.SearchResult
import org.mifos.mobilewallet.mifospay.base.BasePresenter
import org.mifos.mobilewallet.mifospay.base.BaseView

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