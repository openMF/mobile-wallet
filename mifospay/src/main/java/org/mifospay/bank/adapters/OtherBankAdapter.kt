package org.mifospay.bank.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifospay.R
import org.mifospay.domain.model.Bank
import java.util.Locale
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
        holder.ivPopularBank!!.setImageDrawable(
            ContextCompat.getDrawable(context!!, otherBanks!![position].image)
        )
        holder.tvPopularBank!!.text = otherBanks!![position].name
    }

    override fun getItemCount(): Int {
        return if (otherBanks != null) {
            otherBanks!!.size
        } else {
            0
        }
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setData(banks: List<Bank>?) {
        otherBanks = banks
        notifyDataSetChanged()
    }

    fun getBank(position: Int): Bank {
        return otherBanks!![position]
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
                        if (bank.name.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault()))
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
                notifyDataSetChanged()
            }
        }
    }

    fun filterList(filterdNames: List<Bank>?) {
        otherBanks = filterdNames
        notifyDataSetChanged()
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