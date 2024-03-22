package org.mifos.mobilewallet.mifospay.history.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
class HistoryAdapter @Inject constructor() : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var transactions: List<Transaction>? = null
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_casual_list, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions!![position]
        val balance = transaction.amount
        val currencyCode = transaction.currency.code
        holder.tvTransactionAmount
            ?.setText(getFormattedAccountBalance(balance, currencyCode))
        holder.tvTransactionDate!!.text = transaction.date
        if (balance > 0 && context != null) {
            val color = ContextCompat.getColor(context!!, R.color.colorAccentBlue)
            holder.tvTransactionAmount!!.setTextColor(color)
        }
        when (transaction.transactionType) {
            TransactionType.DEBIT -> {
                holder.tvTransactionStatus!!.text = Constants.DEBIT
                holder.tvTransactionStatus!!.setTextColor(
                    ContextCompat.getColor(
                        context!!, R.color.colorDebit
                    )
                )
            }

            TransactionType.CREDIT -> {
                holder.tvTransactionStatus!!.text = Constants.CREDIT
                holder.tvTransactionStatus!!.setTextColor(
                    ContextCompat.getColor(
                        context!!, R.color.colorCredit
                    )
                )
            }

            TransactionType.OTHER -> holder.tvTransactionStatus!!.text = Constants.OTHER
        }
    }

    override fun getItemCount(): Int {
        return if (transactions != null) {
            transactions!!.size
        } else {
            0
        }
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setData(transactions: List<Transaction>?) {
        this.transactions = transactions
        notifyDataSetChanged()
    }

    fun getTransactions(): ArrayList<Transaction>? {
        return transactions as ArrayList<Transaction>?
    }

    fun getTransaction(position: Int): Transaction {
        return transactions!![position]
    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.tv_item_casual_list_title)
        var tvTransactionAmount: TextView? = null

        @JvmField
        @BindView(R.id.tv_item_casual_list_optional_caption)
        var tvTransactionStatus: TextView? = null

        @JvmField
        @BindView(R.id.tv_item_casual_list_subtitle)
        var tvTransactionDate: TextView? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }
}