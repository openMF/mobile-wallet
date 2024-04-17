package org.mifospay.merchants.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifospay.core.model.entity.accounts.savings.SavingsWithAssociations
import org.mifospay.R
import org.mifospay.utils.TextDrawable
import java.util.*
import javax.inject.Inject

class MerchantsAdapter @Inject constructor() : RecyclerView.Adapter<MerchantsAdapter.ViewHolder>(),
    Filterable {
    var merchants: List<SavingsWithAssociations>? = null
        private set
    private var mMerchantsFilteredList: List<SavingsWithAssociations>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_casual_list,
            parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mMerchant = merchants?.get(position)
        val iconDrawable = mMerchant?.clientName
            ?.substring(0, 1)?.let {
                TextDrawable.builder().beginConfig()
                .endConfig().buildRound(
                        it, R.color.colorAccentBlack
                )
            }
        holder.mTvMerchantIcon?.setImageDrawable(iconDrawable)
        holder.mTvMerchantName?.text = mMerchant?.clientName
        holder.mTvMerchantExternalId?.text = mMerchant?.externalId
    }

    override fun getItemCount(): Int {
        return if (merchants != null) merchants!!.size else 0
    }

    fun setData(mMerchantsList: List<SavingsWithAssociations>?) {
        merchants = mMerchantsList
        notifyItemChanged(merchants?.size?.minus(1) ?: 0)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    mMerchantsFilteredList = merchants
                } else {
                    val filteredList: MutableList<SavingsWithAssociations> = ArrayList()
                    for (merchant in merchants!!) {
                        if (merchant.clientName.toLowerCase(Locale.ROOT).contains(
                                charString.toLowerCase(Locale.ROOT)
                            )
                            || merchant.externalId.toLowerCase(Locale.ROOT).contains(
                                charString.toLowerCase(Locale.ROOT)
                            )
                        ) {
                            filteredList.add(merchant)
                        }
                    }
                    mMerchantsFilteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mMerchantsFilteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                mMerchantsFilteredList = results.values as List<SavingsWithAssociations>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.iv_item_casual_list_icon)
        var mTvMerchantIcon: ImageView? = null

        @JvmField
        @BindView(R.id.tv_item_casual_list_title)
        var mTvMerchantName: TextView? = null

        @JvmField
        @BindView(R.id.tv_item_casual_list_subtitle)
        var mTvMerchantExternalId: TextView? = null

        init {
            if (v != null) {
                ButterKnife.bind(this, v)
            }
        }
    }
}