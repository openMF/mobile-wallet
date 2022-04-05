package org.mifos.mobilewallet.mifospay.common.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.core.domain.model.SearchResult
import org.mifos.mobilewallet.mifospay.R
import javax.inject.Inject

/**
 * Created by naman on 21/8/17.
 */
class SearchAdapter @Inject constructor() : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var results: MutableList<SearchResult>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvSearchResult!!.text = results!![position].resultName
    }

    override fun getItemCount(): Int {
        return if (results != null) {
            results!!.size
        } else {
            0
        }
    }

    fun setData(results: MutableList<SearchResult>?) {
        this.results = results
        notifyDataSetChanged()
    }

    fun clearData() {
        results!!.clear()
        notifyDataSetChanged()
    }

    fun getResults(): List<SearchResult>? {
        return results
    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.tv_search_result)
        var tvSearchResult: TextView? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }
}