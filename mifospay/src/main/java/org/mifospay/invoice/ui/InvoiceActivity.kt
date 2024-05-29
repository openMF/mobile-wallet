package org.mifospay.invoice.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseActivity
import org.mifospay.theme.MifosTheme

@AndroidEntryPoint
class InvoiceActivity : BaseActivity() {
    //    @JvmField
//    @Inject
//    var mPresenter: InvoicePresenter? = null
//    var mInvoicePresenter: InvoiceContract.InvoicePresenter? = null
//
//    @JvmField
//    @BindView(R.id.tv_merchantId)
//    var mTvMerchantId: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_consumerId)
//    var mTvConsumerId: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_amount)
//    var mTvAmount: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_itemsBought)
//    var mTvItemsBought: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_status)
//    var mTvStatus: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_transaction_id)
//    var mTvTransactionId: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_paymentLink)
//    var mTvPaymentLink: TextView? = null
//
//    @JvmField
//    @BindView(R.id.tv_receiptLink)
//    var mTvReceiptLink: TextView? = null
//
//    @JvmField
//    @BindView(R.id.v_url)
//    var mVUrl: View? = null
//
//    @JvmField
//    @BindView(R.id.ll_url)
//    var mLlUrl: LinearLayout? = null
//
//    @JvmField
//    @BindView(R.id.tv_date)
//    var mTvDate: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_invoice)
//        ButterKnife.bind(this)
//        setToolbarTitle(Constants.INVOICE)
//        showColoredBackButton(R.drawable.ic_arrow_back_black_24dp)
        val data = intent.data
        setContent {
            MifosTheme {
                InvoiceDetailScreen(data = data,onBackPress = { finish() })
            }
        }
//        if (data != null) {
//            showProgressDialog(Constants.PLEASE_WAIT)
//            mInvoicePresenter!!.getInvoiceDetails(data)
//        } else {
//            finish()
//        }
    }

//    override fun showInvoiceDetails(invoice: Invoice?, merchantId: String?, paymentLink: String?) {
//        mTvMerchantId!!.text = Constants.MERCHANT + ": " + merchantId
//        mTvConsumerId!!.text = (Constants.CONSUMER + ": " + invoice!!.consumerName + " "
//                + invoice.consumerId)
//        mTvAmount!!.text = Constants.AMOUNT + ": " + Constants.INR + " " + invoice.amount + ""
//        mTvItemsBought!!.text = Constants.ITEMS + ": " + invoice.itemsBought
//        var status = Constants.PENDING
//        if (invoice.status == 1L) {
//            status = Constants.DONE
//            mTvTransactionId!!.visibility = View.VISIBLE
//            mVUrl!!.visibility = View.VISIBLE
//            mLlUrl!!.visibility = View.VISIBLE
//            mTvTransactionId!!.text = Constants.TRANSACTION_ID + ": " + invoice.transactionId
//            mTvReceiptLink!!.text = Constants.RECEIPT_DOMAIN + invoice.transactionId
//            mTvReceiptLink!!.setOnLongClickListener {
//                val cm = getSystemService(
//                    CLIPBOARD_SERVICE
//                ) as ClipboardManager
//                val clipData = ClipData.newPlainText(
//                    Constants.UNIQUE_RECEIPT_LINK,
//                    mTvReceiptLink!!.text.toString()
//                )
//                cm.setPrimaryClip(clipData)
//                showSnackbar(Constants.UNIQUE_RECEIPT_LINK_COPIED_TO_CLIPBOARD)
//                true
//            }
//            mTvReceiptLink!!.setOnClickListener {
//                val intent = Intent(this@InvoiceActivity, ReceiptActivity::class.java)
//                intent.data = Uri.parse(
//                    Constants.RECEIPT_DOMAIN + invoice.transactionId
//                )
//                startActivity(intent)
//            }
//        }
//        mTvStatus!!.text = Constants.STATUS + ": " + status
//        mTvPaymentLink!!.text = paymentLink
//        mTvPaymentLink!!.setOnLongClickListener {
//            val cm = getSystemService(
//                CLIPBOARD_SERVICE
//            ) as ClipboardManager
//            val clipData = ClipData.newPlainText(
//                Constants.UNIQUE_PAYMENT_LINK,
//                mTvPaymentLink!!.text.toString()
//            )
//            cm.setPrimaryClip(clipData)
//            showSnackbar(Constants.UNIQUE_PAYMENT_LINK_COPIED_TO_CLIPBOARD)
//            true
//        }
//        mTvDate!!.text = DateHelper.getDateAsString(invoice.date)
//        hideProgressDialog()
//    }
}