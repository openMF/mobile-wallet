package org.mifospay.common.ui

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import com.mifospay.core.model.domain.SearchResult
import org.mifospay.MifosPayApp
import org.mifospay.R
import org.mifospay.base.BaseActivity
import org.mifospay.common.SearchContract
import org.mifospay.common.presenter.SearchPresenter
import org.mifospay.common.ui.adapter.SearchAdapter
import org.mifospay.utils.Toaster
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
        showColoredBackButton(R.drawable.ic_arrow_back_black_24dp)
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