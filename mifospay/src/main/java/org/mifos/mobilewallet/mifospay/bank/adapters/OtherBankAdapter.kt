package org.mifos.mobilewallet.mifospay.bank.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.domain.model.Bank
import java.util.*
import javax.inject.Inject

/**
 * Created by naman on 20/6/17.
 */
class OtherBankAdapter @Inject constructor() : RecyclerView.Adapter<OtherBankAdapter.ViewHolder>(),
    Filterable {
    private var context: Context? = null
    private var otherBanks: List<Bank>? = null
    private var filteredBanks: List<Bank>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_other_banks, parent,
            false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivPopularBank?.setImageDrawable(
            context?.let {
                otherBanks?.get(position)
                    ?.let { it1 -> ContextCompat.getDrawable(it, it1.image) }
            }
        )
        holder.tvPopularBank?.text = otherBanks?.get(position)?.name
    }

    override fun getItemCount(): Int {
        return otherBanks?.size ?: 0
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setData(banks: List<Bank>?) {
        otherBanks = banks
        notifyItemChanged(banks?.size?.minus(1) ?: 0)
    }

    fun getBank(position: Int): Bank? {
        return otherBanks?.get(position)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                filteredBanks = if (charString.isEmpty()) {
                    otherBanks
                } else {
                    val filteredList: MutableList<Bank> = ArrayList()
                    for (bank in otherBanks!!) {
                        if (bank.name.toLowerCase(Locale.ROOT)
                                .contains(charString.toLowerCase(Locale.ROOT))
                        ) {
                            filteredList.add(bank)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredBanks
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredBanks = results.values as List<Bank>
                notifyItemChanged(filteredBanks?.size?.minus(1) ?: 0)
            }
        }
    }

    fun filterList(filterdNames: List<Bank>?) {
        otherBanks = filterdNames

    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.iv_popular_bank)
        var ivPopularBank: ImageView? = null

        @JvmField
        @BindView(R.id.tv_popular_bank)
        var tvPopularBank: TextView? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }
}