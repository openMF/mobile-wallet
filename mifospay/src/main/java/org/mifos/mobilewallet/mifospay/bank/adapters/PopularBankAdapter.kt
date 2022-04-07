package org.mifos.mobilewallet.mifospay.bank.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.domain.model.Bank
import javax.inject.Inject

/**
 * Created by naman on 20/6/17.
 */
class PopularBankAdapter @Inject constructor() :
    RecyclerView.Adapter<PopularBankAdapter.ViewHolder>() {
    private var context: Context? = null
    private var popularBanks: List<Bank>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_popular_banks,
            parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivPopularBank?.setImageDrawable(
            context?.let {
                popularBanks?.get(position)?.let { it1 ->
                    ContextCompat
                        .getDrawable(it, it1.image)
                }
            }
        )
        holder.tvPopularBank?.text = popularBanks?.get(position)?.name
    }

    override fun getItemCount(): Int {
        return popularBanks?.size ?: 0
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setData(banks: List<Bank>?) {
        popularBanks = banks
        if (banks != null) {
            notifyItemChanged(banks.size - 1)
        }
    }

    fun getBank(position: Int): Bank? {
        return popularBanks?.get(position)
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