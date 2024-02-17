package org.mifos.mobilewallet.mifospay.history.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance
import javax.inject.Inject

/**
 * Created by ankur on 18/June/2018
 */
class SpecificTransactionsAdapter @Inject constructor() :
    RecyclerView.Adapter<SpecificTransactionsAdapter.ViewHolder>() {
    private var context: Context? = null
    private var transactions: List<Transaction>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_specific_transaction, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions!![position]
        holder.mTvTransactionId!!.text =
            Constants.TRANSACTION_ID + ": " + transaction.transactionId
        holder.mTvTransactionDate!!.text =
            Constants.DATE + ": " + transaction.date
        holder.mTvTransactionAmount!!.text = getFormattedAccountBalance(
            transaction.amount, transaction.currency.code
        )
        holder.mTvFromClientName!!.text = transaction.transferDetail.fromClient.displayName
        holder.mTvFromAccountNo!!.text = transaction.transferDetail.fromAccount.accountNo
        holder.mTvToClientName!!.text = transaction.transferDetail.toClient.displayName
        holder.mTvToAccountNo!!.text = transaction.transferDetail.toAccount.accountNo
        when (transaction.transactionType) {
            TransactionType.DEBIT -> {
                holder.mTvTransactionStatus!!.text = Constants.DEBIT
                holder.mTvTransactionAmount!!.setTextColor(Color.RED)
            }

            TransactionType.CREDIT -> {
                holder.mTvTransactionStatus!!.text = Constants.CREDIT
                holder.mTvTransactionAmount!!.setTextColor(Color.parseColor("#009688"))
            }

            TransactionType.OTHER -> {
                holder.mTvTransactionStatus!!.text = Constants.OTHER
                holder.mTvTransactionAmount!!.setTextColor(Color.YELLOW)
            }
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
        @BindView(R.id.iv_transaction_status)
        var mIvTransactionStatus: ImageView? = null

        @JvmField
        @BindView(R.id.tv_transaction_id)
        var mTvTransactionId: TextView? = null

        @JvmField
        @BindView(R.id.tv_transaction_date)
        var mTvTransactionDate: TextView? = null

        @JvmField
        @BindView(R.id.tv_transaction_status)
        var mTvTransactionStatus: TextView? = null

        @JvmField
        @BindView(R.id.tv_transaction_amount)
        var mTvTransactionAmount: TextView? = null

        @JvmField
        @BindView(R.id.iv_fromImage)
        var mIvFromImage: ImageView? = null

        @JvmField
        @BindView(R.id.tv_fromClientName)
        var mTvFromClientName: TextView? = null

        @JvmField
        @BindView(R.id.tv_fromAccountNo)
        var mTvFromAccountNo: TextView? = null

        @JvmField
        @BindView(R.id.ll_from)
        var mLlFrom: LinearLayout? = null

        @JvmField
        @BindView(R.id.iv_toImage)
        var mIvToImage: ImageView? = null

        @JvmField
        @BindView(R.id.tv_toClientName)
        var mTvToClientName: TextView? = null

        @JvmField
        @BindView(R.id.tv_toAccountNo)
        var mTvToAccountNo: TextView? = null

        @JvmField
        @BindView(R.id.ll_to)
        var mLlTo: LinearLayout? = null

        @JvmField
        @BindView(R.id.rl_from_to)
        var mRlFromTo: RelativeLayout? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }
}