package org.mifos.mobilewallet.mifospay.history.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.history.HistoryContract
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionDetailView
import org.mifos.mobilewallet.mifospay.history.presenter.TransactionDetailPresenter
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance
import javax.inject.Inject

/**
 * Created by ankur on 06/June/2018
 */
@AndroidEntryPoint
class TransactionDetailDialog : BottomSheetDialogFragment(), TransactionDetailView {
    @JvmField
    @Inject
    var mPresenter: TransactionDetailPresenter? = null
    var mTransactionDetailPresenter: HistoryContract.TransactionDetailPresenter? = null

    @JvmField
    @BindView(R.id.tv_transaction_id)
    var tvTransactionId: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_date)
    var tvTransactionDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_receiptId)
    var tvReceiptId: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_status)
    var tvTransactionStatus: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_amount)
    var tvTransactionAmount: TextView? = null

    @JvmField
    @BindView(R.id.rl_from_to)
    var rlFromTo: RelativeLayout? = null

    @JvmField
    @BindView(R.id.iv_fromImage)
    var ivFromImage: ImageView? = null

    @JvmField
    @BindView(R.id.tv_fromClientName)
    var tvFromClientName: TextView? = null

    @JvmField
    @BindView(R.id.tv_fromAccountNo)
    var tvFromAccountNo: TextView? = null

    @JvmField
    @BindView(R.id.iv_toImage)
    var ivToImage: ImageView? = null

    @JvmField
    @BindView(R.id.tv_toClientName)
    var tvToClientName: TextView? = null

    @JvmField
    @BindView(R.id.tv_toAccountNo)
    var tvToAccountNo: TextView? = null

    @JvmField
    @BindView(R.id.v_rule2)
    var vRule2: View? = null

    @JvmField
    @BindView(R.id.tv_viewReceipt)
    var tvViewReceipt: TextView? = null

    @JvmField
    @BindView(R.id.ll_from)
    var mLlFrom: LinearLayout? = null

    @JvmField
    @BindView(R.id.ll_to)
    var mLlTo: LinearLayout? = null

    @JvmField
    @BindView(R.id.pb_transaction_detail)
    var progressBar: ProgressBar? = null
    var unbinder: Unbinder? = null

    @JvmField
    @BindView(R.id.ll_main)
    var mLlMain: LinearLayout? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var transaction: Transaction? = null
    private var accountNo: String? = null
    private var transactions: ArrayList<Transaction>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.dialog_transaction_detail, null)
        dialog.setContentView(view)
        mBottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        ButterKnife.bind(this, view)
        val bundle = this.arguments
        if (bundle != null) {
            transaction = bundle.getParcelable(Constants.TRANSACTION)
            accountNo = bundle.getString(Constants.ACCOUNT_NUMBER)
            transactions = bundle.getParcelableArrayList(Constants.TRANSACTIONS)
            if (transaction == null) {
                return dialog
            }
        }
        mPresenter!!.attachView(this)
        tvTransactionId!!.text =
            Constants.TRANSACTION_ID + ": " + transaction!!.transactionId
        tvTransactionDate!!.text =
            Constants.DATE + ": " + transaction!!.date
        tvTransactionAmount!!.text =
            getFormattedAccountBalance(
                transaction!!.amount, transaction!!.currency.code
            )
        mPresenter!!.getTransferDetail(transaction!!.transferId)
        if (transaction!!.receiptId != null) {
            tvReceiptId!!.visibility = View.VISIBLE
            tvReceiptId!!.text =
                Constants.RECEIPT_ID + ": " + transaction!!.receiptId
        }
        when (transaction!!.transactionType) {
            TransactionType.DEBIT -> {
                tvTransactionStatus!!.text = Constants.DEBIT
                tvTransactionAmount!!.setTextColor(Color.RED)
            }

            TransactionType.CREDIT -> {
                tvTransactionStatus!!.text = Constants.CREDIT
                tvTransactionAmount!!.setTextColor(Color.parseColor("#009688"))
            }

            TransactionType.OTHER -> {
                tvTransactionStatus!!.text = Constants.OTHER
                tvTransactionAmount!!.setTextColor(Color.YELLOW)
            }
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @OnClick(R.id.tv_viewReceipt)
    fun viewReceipt() {
        val intent = Intent(activity, ReceiptActivity::class.java)
        intent.data =
            Uri.parse(Constants.RECEIPT_DOMAIN + transaction!!.transactionId)
        startActivity(intent)
    }

    @OnClick(R.id.ll_from)
    fun onFromViewClicked() {
        val intent = Intent(activity, SpecificTransactionsActivity::class.java)
        intent.putParcelableArrayListExtra(Constants.TRANSACTIONS, transactions)
        intent.putExtra(Constants.ACCOUNT_NUMBER, tvFromAccountNo!!.text.toString())
        startActivity(intent)
    }

    @OnClick(R.id.ll_to)
    fun onToViewClicked() {
        val intent = Intent(activity, SpecificTransactionsActivity::class.java)
        intent.putParcelableArrayListExtra(Constants.TRANSACTIONS, transactions)
        intent.putExtra(Constants.ACCOUNT_NUMBER, tvToAccountNo!!.text.toString())
        startActivity(intent)
    }

    override fun showTransferDetail(transferDetail: TransferDetail?) {
        if (transaction!!.transferId != 0L) {
            rlFromTo!!.visibility = View.VISIBLE
            vRule2!!.visibility = View.VISIBLE
            tvFromClientName!!.text = transferDetail?.fromClient?.displayName
            tvFromAccountNo!!.text = transferDetail?.fromAccount?.accountNo
            tvToClientName!!.text = transferDetail?.toClient?.displayName
            tvToAccountNo!!.text = transferDetail?.toAccount?.accountNo
        }
    }

    override fun showProgressBar() {
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar!!.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }

    override fun setPresenter(presenter: HistoryContract.TransactionDetailPresenter?) {
        mTransactionDetailPresenter = presenter
    }

}