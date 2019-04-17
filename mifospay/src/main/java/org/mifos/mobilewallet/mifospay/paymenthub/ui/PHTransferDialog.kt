package org.mifos.mobilewallet.mifospay.paymenthub.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View

import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity

import javax.inject.Inject

import kotlinx.android.synthetic.main.dialog_ph_transaction.*
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionStatus
import org.mifos.mobilewallet.mifospay.paymenthub.TransactionContract
import org.mifos.mobilewallet.mifospay.paymenthub.presenter.TransactionPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import android.os.Handler
import kotlinx.android.synthetic.main.dialog_ph_transaction.view.*


/**
 * Created by naman on 30/8/17.
 */

class PHTransferDialog : BottomSheetDialogFragment(), TransactionContract.TransactionView {

    @set:Inject
    lateinit var mPresenter: TransactionPresenter

    lateinit var mTransactionPresenter: TransactionContract.TransactionPresenter

    private var mBehavior: BottomSheetBehavior<*>? = null

    private lateinit var transaction: Transaction
    private var transactionSuccessful = false
    private var handler = Handler()
    private var transactionStatusRunnable: Runnable? = null
    private lateinit var dialogView: View

    companion object {
        fun newInstance(transaction: Transaction): PHTransferDialog
         = PHTransferDialog().apply {
            arguments = Bundle().apply  { putParcelable(Constants.TRANSACTION, transaction) }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).activityComponent.inject(this)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialogView = View.inflate(context, R.layout.dialog_ph_transaction, null)

        dialog.setContentView(dialogView)
        mBehavior = BottomSheetBehavior.from(dialogView.parent as View)

        mPresenter.attachView(this)

        transaction = arguments!!.getParcelable<Transaction>(Constants.TRANSACTION)?: return dialog

        toggleLoading(false)
        dialogView.tv_amount.text = "${transaction.amount.amount} ${transaction.amount.currency}"
        dialogView.tv_client_vpa.text = "${transaction.payee.partyIdInfo.partyIdType}: ${transaction.payee.partyIdInfo.partyIdentifier}"
        dialogView.tv_client_name.text = transaction.payee.name

        dialogView.btn_cancel.setOnClickListener { dismiss() }

        dialogView.btn_confirm.setOnClickListener {
            dialogView.tv_transfer_status.text = "Initiating payment..."
            toggleLoading(true)
            mPresenter.createTransaction(transaction)
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()
        mBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun setPresenter(presenter: TransactionContract.TransactionPresenter) {
        this.mTransactionPresenter = presenter
    }


    override fun showTransactionStatus(transactionStatus: TransactionStatus) {
        if (transactionStatus.transferState == "COMMITTED") {
            dialogView.view_transfer_success.visibility = View.VISIBLE
            toggleLoading(false)
            dialogView.tv_transfer_status.text = "Payment successful"
            transactionSuccessful = true
        } else if (transactionStatus.transferState == "ABORTED") {
            toggleLoading(false)
            showTransactionError("Payment aborted")
        } else {
            handler.postDelayed(transactionStatusRunnable, 3000)
        }
    }

    override fun transactionCreated(transactionInfo: TransactionInfo) {
        dialogView.tv_transfer_status.text = "Payment initiated. Waiting for payment confirmation..."
        toggleLoading(true)

        transactionStatusRunnable = Runnable {
            mPresenter.fetchTransactionInfo(transaction.clientRefId)
        }

        handler.postDelayed(transactionStatusRunnable, 1000)

    }

    override fun showQR(qrString: String) {}

    override fun qrDecoded(qrData: QRData) {}

    private fun toggleLoading(loading: Boolean) {
        if (loading) {
            dialogView.progressBar.visibility = View.VISIBLE
            dialogView.progressBar.startRippleAnimation()
            dialogView.contentView.setVisibility(View.GONE)
        } else {
            dialogView.progressBar.visibility = View.GONE
            dialogView.progressBar.stopRippleAnimation()
            dialogView.contentView.setVisibility(View.VISIBLE)
        }
    }

    override fun showTransactionError(message: String) {
        dialogView.view_transfer_failure.visibility = View.VISIBLE
        dialogView.tv_transfer_status.text = message
        toggleLoading(false)
    }

}