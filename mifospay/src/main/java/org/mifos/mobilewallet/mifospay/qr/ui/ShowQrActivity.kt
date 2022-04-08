package org.mifos.mobilewallet.mifospay.qr.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.*
import android.widget.Toast
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityShowQrBinding
import org.mifos.mobilewallet.mifospay.qr.QrContract
import org.mifos.mobilewallet.mifospay.qr.QrContract.ShowQrView
import org.mifos.mobilewallet.mifospay.qr.presenter.ShowQrPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by naman on 8/7/17.
 */
class ShowQrActivity : BaseActivity(), ShowQrView {
    private lateinit var mPresenter: ShowQrPresenter
    private lateinit var mShowQrPresenter: QrContract.ShowQrPresenter
    private lateinit var mAmount: String
    private lateinit var mBitmap: Bitmap
    private lateinit var binding: ActivityShowQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowQrBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setToolbarTitle(Constants.QR_CODE)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter.attachView(this)
        val qrData = intent.getStringExtra(Constants.QR_DATA)
        mShowQrPresenter.generateQr(qrData)
        binding.tvQrVpa.text = String.format("%s: %s", getString(R.string.vpa), qrData)
        val layout = window.attributes
        layout.screenBrightness = 1f
        window.attributes = layout
        binding.btnSetAmount.setOnClickListener { showSetAmountDialog(qrData) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share_qr, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_share_qr ->
                val imageUri = saveImage(mBitmap)
                shareQr(imageUri)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun saveImage(bitmap: Bitmap): Uri? {
        val imagesFolder = File(cacheDir, "codes")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_code.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                this,
                "org.mifos.mobilewallet.mifospay.provider", file
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    private fun shareQr(uri: Uri?) {
        if (uri != null) {
            var intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/png"
            intent = Intent.createChooser(intent, "Share Qr code")
            startActivity(intent)
        }
    }

    private fun showSetAmountDialog(qrData: String?) {
        val editTextDialog = AlertDialog.Builder(this)
        editTextDialog.setCancelable(false)
        editTextDialog.setTitle("Enter Amount")
        val view = LayoutInflater.from(applicationContext).inflate(
            R.layout.dialog_set_amt,
            findViewById<View>(android.R.id.content) as ViewGroup,
            false
        )
        val edittext: TextInputEditText = view.findViewById(R.id.editText_set_amt)
        edittext.inputType = InputType.TYPE_CLASS_NUMBER
        editTextDialog.setView(view)
        edittext.setText(mAmount)
        editTextDialog.setPositiveButton(
            R.string.confirm,
            DialogInterface.OnClickListener { dialog, which ->
                val amount = edittext.text.toString()
                if (amount == "") {
                    showToast(getString(R.string.enter_amount))
                    return@OnClickListener
                } else if (amount.toDouble() <= 0) {
                    showToast(Constants.PLEASE_ENTER_VALID_AMOUNT)
                    return@OnClickListener
                }
                mAmount = amount
                binding.tvQrVpa.text = String.format(
                    "%s: %s\n%s: %s",
                    getString(R.string.vpa),
                    qrData,
                    getString(R.string.amount),
                    mAmount
                )
                generateQR("$qrData, $mAmount")
            })
        editTextDialog.setNeutralButton(R.string.reset) { dialog, which ->
            mAmount = null.toString()
            binding.tvQrVpa.text = String.format("%s: %s", getString(R.string.vpa), qrData)
            generateQR(qrData)
            showToast("Reset Amount Successful")
        }
        editTextDialog.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        editTextDialog.show()
    }

    override fun setPresenter(presenter: QrContract.ShowQrPresenter?) {
        if (presenter != null) {
            mShowQrPresenter = presenter
        }
    }

    override fun showGeneratedQr(bitmap: Bitmap?) {
        binding.ivQrCode.setImageBitmap(bitmap)
        if (bitmap != null) {
            mBitmap = bitmap
        }
    }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun generateQR(qrData: String?) {
        mShowQrPresenter.generateQr(qrData)
    }
}