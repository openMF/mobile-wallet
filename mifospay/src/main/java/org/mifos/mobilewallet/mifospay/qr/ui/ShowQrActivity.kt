package org.mifos.mobilewallet.mifospay.qr.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import android.text.InputType
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.qr.QrContract
import org.mifos.mobilewallet.mifospay.qr.QrContract.ShowQrView
import org.mifos.mobilewallet.mifospay.qr.presenter.ShowQrPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * Created by naman on 8/7/17.
 */
@AndroidEntryPoint
class ShowQrActivity : BaseActivity(), ShowQrView {
    @JvmField
    @Inject
    var mPresenter: ShowQrPresenter? = null
    var mShowQrPresenter: QrContract.ShowQrPresenter? = null

    @JvmField
    @BindView(R.id.iv_qr_code)
    var ivQrCode: ImageView? = null

    @JvmField
    @BindView(R.id.tv_qr_vpa)
    var tvQrData: TextView? = null

    @JvmField
    @BindView(R.id.btn_set_amount)
    var btnSetAmount: Button? = null
    private var mAmount: String? = null
    private var mBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_qr)
        ButterKnife.bind(this@ShowQrActivity)
        setToolbarTitle(Constants.QR_CODE)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter!!.attachView(this)
        val qrData = intent.getStringExtra(Constants.QR_DATA)
        mShowQrPresenter!!.generateQr(qrData)
        tvQrData!!.text = String.format("%s: %s", getString(R.string.vpa), qrData)
        val layout = window.attributes
        layout.screenBrightness = 1f
        window.attributes = layout
        btnSetAmount!!.setOnClickListener(View.OnClickListener { showSetAmountDialog(qrData) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share_qr, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_share_qr -> if (mBitmap != null) {
                val imageUri = saveImage(mBitmap!!)
                shareQr(imageUri)
            }
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

    fun showSetAmountDialog(qrData: String?) {
        val editTextDialog = AlertDialog.Builder(this)
        editTextDialog.setCancelable(false)
        editTextDialog.setTitle("Enter Amount")
        val view = LayoutInflater.from(applicationContext).inflate(
            R.layout.dialog_set_amt,
            findViewById<View>(android.R.id.content) as ViewGroup,
            false
        )
        val edittext = view.findViewById<TextInputEditText>(R.id.editText_set_amt)
        edittext.inputType = InputType.TYPE_CLASS_NUMBER
        editTextDialog.setView(view)
        if (mAmount != null) {
            edittext.setText(mAmount)
        }
        editTextDialog.setPositiveButton(R.string.confirm,
            DialogInterface.OnClickListener { dialog, which ->
                val amount = edittext.text.toString().trim { it <= ' ' }
                if (amount == "") {
                    showToast(getString(R.string.enter_amount))
                    return@OnClickListener
                } else if (amount.toDouble() <= 0) {
                    showToast(Constants.PLEASE_ENTER_VALID_AMOUNT)
                    return@OnClickListener
                }
                mAmount = amount
                tvQrData!!.text = String.format(
                    "%s: %s\n%s: %s",
                    getString(R.string.vpa),
                    qrData,
                    getString(R.string.amount),
                    mAmount
                )
                generateQR("$qrData, $mAmount")
            })
        editTextDialog.setNeutralButton(R.string.reset,
            DialogInterface.OnClickListener { dialog, which ->
                mAmount = null
                tvQrData!!.text = String.format("%s: %s", getString(R.string.vpa), qrData)
                generateQR(qrData)
                showToast("Reset Amount Successful")
            })
        editTextDialog.setNegativeButton(R.string.cancel,
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        editTextDialog.show()
    }

    override fun setPresenter(presenter: QrContract.ShowQrPresenter?) {
        mShowQrPresenter = presenter
    }

    override fun showGeneratedQr(bitmap: Bitmap?) {
        ivQrCode!!.setImageBitmap(bitmap)
        mBitmap = bitmap
    }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun generateQR(qrData: String?) {
        mShowQrPresenter!!.generateQr(qrData)
    }
}