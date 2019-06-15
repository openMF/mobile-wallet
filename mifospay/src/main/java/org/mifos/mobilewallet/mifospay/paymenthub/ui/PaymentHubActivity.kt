package org.mifos.mobilewallet.mifospay.paymenthub.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_payment_hub.*
import org.mifos.mobilewallet.core.data.paymenthub.entity.QRData
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionStatus
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment
import org.mifos.mobilewallet.mifospay.paymenthub.TransactionContract
import org.mifos.mobilewallet.mifospay.paymenthub.presenter.TransactionPresenter
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import javax.inject.Inject
import java.util.UUID


class PaymentHubActivity : BaseActivity(), TransactionContract.TransactionView {

    private val REQUEST_CAMERA = 0
    private val SCAN_QR_REQUEST_CODE = 666

    @set: Inject
    lateinit var mPresenter: TransactionPresenter

    lateinit var mTransactionPresenter: TransactionContract.TransactionPresenter

    private val currentUser = PHLoginActivity.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        mPresenter.attachView(this)
        setContentView(R.layout.activity_payment_hub)
        setSupportActionBar(toolbar.apply {
            title = "Mifos Pay Payment Hub"
        })

        btn_scan_qr.setOnClickListener { scanQr() }

        btn_show_qr.setOnClickListener { showQr() }
    }

    private fun showQr() {
        if (currentUser == null) return
        val userBank = currentUser.banks.get(0)
        val transactionId = createTransactionID()

        val qrData = QRData(userBank.partyIdInfo.partyIdType!!,
                userBank.partyIdInfo.partyIdentifier!!,
                currentUser.firstName + " " + currentUser.lastName,
                "0000",
                et_amount.text.toString(),
                et_desc.text.toString(),
                "IDR",
                transactionId,
                "https://webshop.dpc.hu/orderId=${transactionId}")

        mPresenter.createQRData(qrData)
    }

    private fun scanQr() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            val i = Intent(this, ReadQrActivity::class.java)
            startActivityForResult(i, SCAN_QR_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val qrData = data!!.getStringExtra(Constants.QR_DATA)
            mPresenter.decodeQRData(qrData)
        }
    }

    override fun setPresenter(presenter: TransactionContract.TransactionPresenter) {
        mTransactionPresenter = presenter
    }

    override fun showQR(qrString: String) {
        val intent = Intent(this, ShowQrActivity::class.java)
        intent.putExtra(Constants.QR_DATA, qrString)
        startActivity(intent)
    }

    override fun qrDecoded(qrData: QRData) {
        PHTransferDialog.newInstance(mPresenter.qrDataToTransaction(qrData, currentUser!!))
                .show(supportFragmentManager, "PHTransactionDialog")
    }

    @Throws(Exception::class)
    private fun createTransactionID(): String {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase()
    }

    override fun transactionCreated(transactionInfo: TransactionInfo) {}

    override fun showTransactionStatus(transactionStatus: TransactionStatus) {}

    override fun showTransactionError(message: String) {}
}