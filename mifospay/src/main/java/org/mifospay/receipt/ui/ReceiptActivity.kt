package org.mifospay.receipt.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mobile.passcode.utils.PassCodeConstants
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import org.mifospay.R
import org.mifospay.base.BaseActivity
import org.mifospay.receipt.ReceiptContract
import org.mifospay.receipt.ReceiptContract.ReceiptView
import org.mifospay.receipt.presenter.ReceiptPresenter
import org.mifospay.common.Constants
import org.mifospay.feature.passcode.PassCodeActivity
import org.mifospay.utils.FileUtils
import org.mifospay.utils.Toaster
import org.mifospay.common.Utils.getFormattedAccountBalance
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ReceiptActivity : BaseActivity(), ReceiptView {
    @JvmField
    @Inject
    var mPresenter: ReceiptPresenter? = null
    var mReceiptPresenter: ReceiptContract.ReceiptPresenter? = null

    @JvmField
    @BindView(R.id.tv_amount)
    var tvAmount: TextView? = null

    @JvmField
    @BindView(R.id.tv_operation)
    var tvOperation: TextView? = null

    @JvmField
    @BindView(R.id.tv_name)
    var tvPaidToName: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_ID)
    var tvTransactionID: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_date)
    var tvDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_to_name)
    var tvTransToName: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_to_number)
    var tvTransToNumber: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_from_name)
    var tvTransFromName: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_from_number)
    var tvTransFromNumber: TextView? = null

    @JvmField
    @BindView(R.id.tv_transaction_reciept)
    var tvReceiptLink: TextView? = null
    private var transactionId: String? = null
    private var isDebit = false
    private var deepLinkURI: Uri? = null
    private var shareMessage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        ButterKnife.bind(this)
        setToolbarTitle(Constants.RECEIPT)
        showColoredBackButton(R.drawable.ic_arrow_back_black_24dp)
        mPresenter!!.attachView(this)
        val data = intent.data
        deepLinkURI = data
        if (data != null) {
            val scheme = data.scheme // "https"
            val host = data.host // "receipt.mifospay.com"
            val params: List<String>
            try {
                params = data.pathSegments
                transactionId = params[0] // "transactionId"
                tvReceiptLink!!.text = data.toString()
            } catch (e: IndexOutOfBoundsException) {
                showToast(getString(R.string.invalid_link))
            }
            showProgressDialog(Constants.PLEASE_WAIT)
            mPresenter!!.fetchTransaction(transactionId!!.toLong())
            shareMessage = (Constants.RECEIPT_SHARING_MESSAGE
                    + tvTransFromName!!.text
                    + Constants.TO
                    + tvTransToName!!.text
                    + Constants.COLON
                    + tvReceiptLink!!.text.toString().trim { it <= ' ' })
        }
    }

    override fun openPassCodeActivity() {
        PassCodeActivity.startPassCodeActivity(
            context = this,
            bundle = bundleOf(
                Pair("uri", deepLinkURI.toString()),
                Pair(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
            ),
        )
        finish()
    }

    override fun showTransactionDetail(transaction: Transaction?) {
        tvAmount!!.text = getFormattedAccountBalance(
            transaction!!.amount, transaction.currency.code
        )
        tvDate!!.text = transaction.date
        tvReceiptLink!!.text = Constants.RECEIPT_DOMAIN + transaction.transactionId
        tvTransactionID!!.text = transaction.transactionId.toString()
        when (transaction.transactionType) {
            TransactionType.DEBIT -> {
                isDebit = true
                tvOperation!!.setText(R.string.paid_to)
                tvOperation!!.setTextColor(resources.getColor(R.color.colorDebit))
            }

            TransactionType.CREDIT -> {
                isDebit = false
                tvOperation!!.setText(R.string.credited_by)
                tvOperation!!.setTextColor(resources.getColor(R.color.colorCredit))
            }

            TransactionType.OTHER -> {
                isDebit = false
                tvOperation!!.text = Constants.OTHER
                tvOperation!!.setTextColor(Color.YELLOW)
            }
        }
    }

    override fun showTransferDetail(transferDetail: TransferDetail?) {
        if (isDebit) {
            tvPaidToName!!.text = transferDetail!!.toClient.displayName
        } else {
            tvPaidToName!!.text = transferDetail!!.fromClient.displayName
        }
        tvTransToName!!.text = Constants.NAME + transferDetail.toClient.displayName
        tvTransToNumber!!.text = Constants.ACCOUNT_NUMBER + transferDetail
            .toAccount.accountNo
        tvTransFromName!!.text = Constants.NAME + transferDetail.fromClient.displayName
        tvTransFromNumber!!.text = Constants.ACCOUNT_NUMBER + transferDetail
            .fromAccount.accountNo
        dismissProgressDialog()
    }

    @OnClick(R.id.fab_download)
    fun initiateDownload() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE
            )
        } else {
            // Permission already granted
            val file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + Constants.MIFOSPAY,
                Constants.RECEIPT + transactionId + Constants.PDF
            )
            if (file.exists()) {
                openFile(this@ReceiptActivity, file)
            } else {
                showSnackbar(getString(R.string.downloading_receipt))
                mPresenter!!.downloadReceipt(transactionId!!)
            }
        }
    }

    @OnClick(R.id.transaction_reciept_copy)
    fun copyReceiptLink() {
        val cm = getSystemService(
            CLIPBOARD_SERVICE
        ) as ClipboardManager
        val clipData = ClipData.newPlainText(
            Constants.UNIQUE_RECEIPT_LINK,
            tvReceiptLink!!.text.toString().trim { it <= ' ' })
        cm.setPrimaryClip(clipData)
        showSnackbar(Constants.UNIQUE_RECEIPT_LINK_COPIED_TO_CLIPBOARD)
    }

    @OnClick(R.id.transaction_reciept_share)
    fun shareReceiptLink() {
        var intent: Intent? = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, shareMessage)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent = Intent.createChooser(intent, getString(R.string.share_receipt))
        startActivity(intent)
    }

    override fun setPresenter(presenter: ReceiptContract.ReceiptPresenter?) {
        mReceiptPresenter = presenter
    }

    fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(findViewById(android.R.id.content), message)
    }

    override fun writeReceiptToPDF(responseBody: ResponseBody?, filename: String?) {
        val mifosDirectory = File(
            Environment.getExternalStorageDirectory(),
            Constants.MIFOSPAY
        )
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs()
        }
        val documentFile = File(mifosDirectory.path, filename)
        if (!FileUtils.writeInputStreamDataToFile(responseBody!!.byteStream(), documentFile)) {
            showToast(Constants.ERROR_DOWNLOADING_RECEIPT)
        } else {
            Toaster.show(findViewById(android.R.id.content),
                Constants.RECEIPT_DOWNLOADED_SUCCESSFULLY, Snackbar.LENGTH_LONG, Constants.VIEW,
                View.OnClickListener { openFile(this@ReceiptActivity, documentFile) })
        }
    }

    private fun openFile(context: Context, file: File) {
        val data = FileProvider.getUriForFile(
            context,
            "org.mifospay.provider", file
        )
        context.grantUriPermission(
            context.packageName,
            data, Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        var intent: Intent? = Intent(Intent.ACTION_VIEW)
            .setDataAndType(data, "application/pdf")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent = Intent.createChooser(intent, getString(R.string.view_receipt))
        context.startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    showSnackbar(getString(R.string.downloading_receipt))
                    mReceiptPresenter!!.downloadReceipt(transactionId)
                } else {
                    showToast(Constants.NEED_EXTERNAL_STORAGE_PERMISSION_TO_DOWNLOAD_RECEIPT)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        dismissProgressDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 48
    }
}