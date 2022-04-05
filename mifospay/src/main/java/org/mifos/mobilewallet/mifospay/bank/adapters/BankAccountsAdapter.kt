package org.mifos.mobilewallet.mifospay.bank.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.core.domain.model.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import javax.inject.Inject

/**
 * Created by ankur on 09/July/2018
 */
class BankAccountsAdapter @Inject constructor() :
    RecyclerView.Adapter<BankAccountsAdapter.ViewHolder>() {
    private var mBankAccountDetailsList: MutableList<BankAccountDetails>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_casual_list,
            parent, false
        )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bankAccountDetails = mBankAccountDetailsList!![position]
        holder.mTvBankName!!.text = bankAccountDetails.bankName
        holder.mTvAccountHolderName!!.text = bankAccountDetails.accountholderName
        holder.mTvBranch!!.text = bankAccountDetails.branch
        holder.imageViewAccount!!.setImageResource(R.drawable.ic_bank)
    }

    override fun getItemCount(): Int {
        return if (mBankAccountDetailsList != null) {
            mBankAccountDetailsList!!.size
        } else {
            0
        }
    }

    fun setData(bankAccountDetailsList: MutableList<BankAccountDetails>?) {
        mBankAccountDetailsList = bankAccountDetailsList
        notifyDataSetChanged()
    }

    fun getBankDetails(position: Int): BankAccountDetails {
        return mBankAccountDetailsList!![position]
    }

    fun addBank(bankAccountDetails: BankAccountDetails) {
        mBankAccountDetailsList!!.add(bankAccountDetails)
        notifyDataSetChanged()
        DebugUtil.log(mBankAccountDetailsList!!.size)
    }

    fun setBankDetails(index: Int, bankAccountDetails: BankAccountDetails) {
        mBankAccountDetailsList!![index] = bankAccountDetails
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        @JvmField
        @BindView(R.id.tv_item_casual_list_title)
        var mTvBankName: TextView? = null

        @JvmField
        @BindView(R.id.tv_item_casual_list_subtitle)
        var mTvAccountHolderName: TextView? = null

        @JvmField
        @BindView(R.id.tv_item_casual_list_optional_caption)
        var mTvBranch: TextView? = null

        @JvmField
        @BindView(R.id.iv_item_casual_list_icon)
        var imageViewAccount: ImageView? = null

        init {
            ButterKnife.bind(this, v!!)
        }
    }

    init {
        mBankAccountDetailsList = ArrayList()
    }
}