package org.mifos.mobilewallet.mifospay.common.ui

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.SearchResult
import org.mifos.mobilewallet.mifospay.MifosPayApp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.common.SearchContract
import org.mifos.mobilewallet.mifospay.common.presenter.SearchPresenter
import org.mifos.mobilewallet.mifospay.common.ui.adapter.SearchAdapter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by naman on 21/8/17.
 */
@AndroidEntryPoint
class SearchActivity : BaseActivity(), SearchContract.SearchView {
    @JvmField
    @Inject
    var mPresenter: SearchPresenter? = null
    var mSearchPresenter: SearchContract.SearchPresenter? = null

    @JvmField
    @BindView(R.id.et_search)
    var etSearch: EditText? = null

    @JvmField
    @BindView(R.id.rv_search_result)
    var rvSearchResults: RecyclerView? = null

    @JvmField
    @Inject
    var searchAdapter: SearchAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ButterKnife.bind(this)
        setToolbarTitle("")
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter?.attachView(this)
        rvSearchResults?.layoutManager =
            LinearLayoutManager(this)
        rvSearchResults?.adapter = searchAdapter
        rvSearchResults?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    @OnTextChanged(R.id.et_search)
    fun searchTextChanged() {
        if (etSearch?.text.toString().length > 3) {
            showSwipeProgress()
            mSearchPresenter?.performSearch(etSearch?.text.toString())
        } else {
            searchAdapter?.clearData()
        }
    }

    override fun setPresenter(presenter: SearchContract.SearchPresenter?) {
        mSearchPresenter = presenter
    }

    override fun showSearchResult(searchResults: MutableList<SearchResult>?) {
        searchAdapter?.setData(searchResults)
        hideSwipeProgress()
    }

    override fun showToast(message: String?) {
        Toaster.showToast(MifosPayApp.context, message)
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(findViewById(android.R.id.content), message)
    }
}