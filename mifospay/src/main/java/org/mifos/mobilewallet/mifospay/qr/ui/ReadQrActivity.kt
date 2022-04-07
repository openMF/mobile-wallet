package org.mifos.mobilewallet.mifospay.qr.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityReadQrBinding
import org.mifos.mobilewallet.mifospay.qr.QrContract
import org.mifos.mobilewallet.mifospay.qr.QrContract.ReadQrView
import org.mifos.mobilewallet.mifospay.qr.presenter.ReadQrPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Created by naman on 7/9/17.
 */
class ReadQrActivity : BaseActivity(), ReadQrView, ZXingScannerView.ResultHandler {
    @JvmField
    @Inject
    var mPresenter: ReadQrPresenter? = null
    private var mReadQrPresenter: QrContract.ReadQrPresenter? = null

    private lateinit var binding: ActivityReadQrBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadQrBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setToolbarTitle(Constants.SCAN_CODE)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter?.attachView(this)
        binding.scannerView.setAutoFocus(true)

        binding.btnFlashOn.setOnClickListener {
            turnOnFlash()
        }

        binding.btnFlashOff.setOnClickListener {
            turnOffFlash()
        }

        binding.btnOpenGallery.setOnClickListener {
            openGallery()
        }

    }

    fun turnOnFlash() {
        binding.scannerView.flash = true
        binding.btnFlashOn.visibility = View.GONE
        binding.btnFlashOff.visibility = View.VISIBLE
    }

    fun turnOffFlash() {
        binding.scannerView.flash = false
        binding.btnFlashOn.visibility = View.VISIBLE
        binding.btnFlashOff.visibility = View.GONE
    }

    fun openGallery() {
        val photoPic = Intent(Intent.ACTION_PICK)
        photoPic.type = "image/*"
        startActivityForResult(photoPic, SELECT_PHOTO)
    }

    public override fun onResume() {
        super.onResume()
        binding.scannerView.setResultHandler(this)
        binding.scannerView.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        binding.scannerView.stopCamera()
    }

    override fun handleResult(result: Result) {
        val qrData = result.text
        val returnIntent = Intent()
        returnIntent.putExtra(Constants.QR_DATA, qrData)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun setPresenter(presenter: QrContract.ReadQrPresenter?) {
        mReadQrPresenter = presenter
    }

    private fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result = reader.decode(bitmap)
            contents = result.text
            val returnIntent = Intent()
            returnIntent.putExtra(Constants.QR_DATA, contents)
            setResult(RESULT_OK, returnIntent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Error! $e", Toast.LENGTH_SHORT
            ).show()
        }
        return contents
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                val selectedImage = data?.data
                if (selectedImage != null) {
                    val imageStream = contentResolver.openInputStream(selectedImage)
                    val bMap = BitmapFactory.decodeStream(imageStream)
                    scanQRImage(bMap)
                }
            } catch (e: FileNotFoundException) {
                Toast.makeText(applicationContext, "File not found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        private const val SELECT_PHOTO = 100
    }
}