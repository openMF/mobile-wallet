package org.mifos.mobilewallet.mifospay.invoice.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mobilewallet.model.entity.Invoice
import com.mifos.mobilewallet.model.utils.DateHelper
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject

/**
 * Created by ankur on 11/June/2018
 */
class InvoicesAdapter @Inject constructor() : RecyclerView.Adapter<InvoicesAdapter.ViewHolder>() {
    private var context: Context? = null
    var invoiceList: List<Invoice>? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.invoice_item, parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invoice = invoiceList!![position]
        holder.mTvInvoiceTitle!!.text = invoice.title
        holder.mTvInvoiceId!!.text = "Invoice Id: " + invoice.id + ""
        holder.mTvInvoiceStatus!!.text =
            if (invoice.status == 0L) Constants.PENDING else Constants.DONE
        holder.mInvoiceImage!!.setImageResource(
            if (invoice.status == 0L) R.drawable.ic_remove_circle_outline_black_24dp
            else R.drawable.ic_check_round_black_24dp
        )
        holder.mTvInvoiceDate!!.text = DateHelper.getDateAsString(invoice.date)
        holder.mTvInvoiceAmount!!.text = Constants.INR + invoice.amount
    }

    override fun getItemCount(): Int {
        return if (invoiceList != null) {
            invoiceList!!.size
        } else {
            0
        }
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setData(invoices: List<Invoice>?) {
        invoiceList = invoices
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.tv_invoice_id)
        var mTvInvoiceId: TextView? = null

        @JvmField
        @BindView(R.id.invoice_status_image)
        var mInvoiceImage: ImageView? = null

        @JvmField
        @BindView(R.id.tv_invoice_date)
        var mTvInvoiceDate: TextView? = null

        @JvmField
        @BindView(R.id.tv_invoice_status)
        var mTvInvoiceStatus: TextView? = null

        @JvmField
        @BindView(R.id.tv_invoice_amount)
        var mTvInvoiceAmount: TextView? = null

        @JvmField
        @BindView(R.id.tv_invoice_title)
        var mTvInvoiceTitle: TextView? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }
}