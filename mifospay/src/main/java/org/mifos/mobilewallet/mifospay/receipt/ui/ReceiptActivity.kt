package org.mifos.mobilewallet.mifospay.receipt.ui

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
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import com.mifos.mobile.passcode.utils.PassCodeConstants
import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.TransferDetail
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityReceiptBinding
import org.mifos.mobilewallet.mifospay.passcode.ui.PassCodeActivity
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract
import org.mifos.mobilewallet.mifospay.receipt.ReceiptContract.ReceiptView
import org.mifos.mobilewallet.mifospay.receipt.presenter.ReceiptPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.FileUtils
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance
import java.io.File

class ReceiptActivity : BaseActivity(), ReceiptView {
    private lateinit var mPresenter: ReceiptPresenter
    private lateinit var mReceiptPresenter: ReceiptContract.ReceiptPresenter

    private lateinit var transactionId: String
    private var isDebit: Boolean = false
    private lateinit var deepLinkURI: Uri
    private lateinit var binding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setToolbarTitle(Constants.RECEIPT)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter.attachView(this)
        val data = intent.data
        if (data != null) {
            deepLinkURI = data
        }
        if (data != null) {
            val scheme = data.scheme // "https"
            val host = data.host // "receipt.mifospay.com"
            val params: List<String>
            try {
                params = data.pathSegments
                transactionId = params[0] // "transactionId"
                binding.tvTransactionReciept.text = data.toString()
            } catch (e: IndexOutOfBoundsException) {
                showToast(getString(R.string.invalid_link))
            }
            showProgressDialog(Constants.PLEASE_WAIT)
            transactionId.toLong().let { mPresenter.fetchTransaction(it) }
        }

        binding.fabDownload.setOnClickListener {
            initiateDownload()
        }

        binding.transactionRecieptCopy.setOnClickListener {
            copyReceiptLink()
        }

        binding.transactionRecieptShare.setOnClickListener {
            shareReceiptLink()
        }
    }

    override fun openPassCodeActivity() {
        val i = Intent(this, PassCodeActivity::class.java)
        i.putExtra("uri", deepLinkURI.toString())
        /**
         * this is actually not true but has to be set true so as to make the passcode
         * open a new receipt activity
         */
        i.putExtra(PassCodeConstants.PASSCODE_INITIAL_LOGIN, true)
        startActivity(i)
        finish()
    }

    override fun showTransactionDetail(transaction: Transaction?) {
        if (transaction != null) {
            binding.tvAmount.text = getFormattedAccountBalance(
                transaction.amount, transaction.currency.code
            )
        }
        if (transaction != null) {
            binding.tvTransactionDate.text = transaction.date
        }
        if (transaction != null) {
            binding.tvTransactionReciept.text =
                Constants.RECEIPT_DOMAIN + transaction.transactionId
        }
        if (transaction != null) {
            binding.tvTransactionID.text = transaction.transactionId.toString()
        }
        if (transaction != null) {
            when (transaction.transactionType) {
                TransactionType.DEBIT -> {
                    isDebit = true
                    binding.tvOperation.setText(R.string.paid_to)
                    binding.tvOperation.setTextColor(resources.getColor(R.color.colorDebit))
                }
                TransactionType.CREDIT -> {
                    isDebit = false
                    binding.tvOperation.setText(R.string.credited_by)
                    binding.tvOperation.setTextColor(resources.getColor(R.color.colorCredit))
                }
                TransactionType.OTHER -> {
                    isDebit = false
                    binding.tvOperation.text = Constants.OTHER
                    binding.tvOperation.setTextColor(Color.YELLOW)
                }
            }
        }
    }

    override fun showTransferDetail(transferDetail: TransferDetail?) {
        if (isDebit) {
            binding.tvName.text = transferDetail?.toClient?.displayName
        } else {
            binding.tvName.text = transferDetail?.fromClient?.displayName
        }
        binding.tvTransactionToName.text = Constants.NAME + transferDetail?.toClient?.displayName
        binding.tvTransactionToNumber.text =
            Constants.ACCOUNT_NUMBER + transferDetail?.toAccount?.accountNo
        binding.tvTransactionFromName.text =
            Constants.NAME + transferDetail?.fromClient?.displayName
        binding.tvTransactionFromNumber.text =
            Constants.ACCOUNT_NUMBER + transferDetail?.fromAccount?.accountNo
        dismissProgressDialog()
    }

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
                mPresenter.downloadReceipt(transactionId)
            }
        }
    }

    fun copyReceiptLink() {
        val cm = getSystemService(
            CLIPBOARD_SERVICE
        ) as ClipboardManager
        val clipData = ClipData.newPlainText(
            Constants.UNIQUE_RECEIPT_LINK,
            binding.tvTransactionReciept.text.toString()
        )
        cm.setPrimaryClip(clipData)
        showSnackbar(Constants.UNIQUE_RECEIPT_LINK_COPIED_TO_CLIPBOARD)
    }

    fun shareReceiptLink() {
        var intent: Intent? = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, binding.tvTransactionReciept.text.toString())
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent = Intent.createChooser(intent, getString(R.string.share_receipt))
        startActivity(intent)
    }

    override fun setPresenter(presenter: ReceiptContract.ReceiptPresenter?) {
        if (presenter != null) {
            mReceiptPresenter = presenter
        }
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
        val documentFile = filename?.let { File(mifosDirectory.path, it) }
        if (responseBody != null) {
            if (!FileUtils.writeInputStreamDataToFile(responseBody.byteStream(), documentFile)) {
                showToast(Constants.ERROR_DOWNLOADING_RECEIPT)
            } else {
                Toaster.show(
                    findViewById(android.R.id.content),
                    Constants.RECEIPT_DOWNLOADED_SUCCESSFULLY, Snackbar.LENGTH_LONG, Constants.VIEW
                ) {
                    if (documentFile != null) {
                        openFile(this@ReceiptActivity, documentFile)
                    }
                }
            }
        }
    }

    private fun openFile(context: Context, file: File) {
        val data = FileProvider.getUriForFile(
            context,
            "org.mifos.mobilewallet.mifospay.provider", file
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
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    showSnackbar(getString(R.string.downloading_receipt))
                    mReceiptPresenter.downloadReceipt(transactionId)
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