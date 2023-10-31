package org.mifos.mobilewallet.mifospay.standinginstruction.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import butterknife.ButterKnife
import com.google.android.gms.common.util.DataUtils
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.databinding.ActivityNewSiBinding
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity
import org.mifos.mobilewallet.mifospay.standinginstruction.StandingInstructionContract
import org.mifos.mobilewallet.mifospay.standinginstruction.presenter.NewSIPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates


@AndroidEntryPoint
class NewSIActivity : BaseActivity(), StandingInstructionContract.NewSIView {


    private val REQUEST_CAMERA = 0
    private val SCAN_QR_REQUEST_CODE = 666

    @Inject
    lateinit var mPresenter: NewSIPresenter
    private lateinit var mNewSIPresenter: StandingInstructionContract.NewSIPresenter

    private var clientId by Delegates.notNull<Long>()

    lateinit var binding: ActivityNewSiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_new_si);
        setContentView(binding.root)
        ButterKnife.bind(this)
        setToolbarTitle(getString(R.string.tile_si_activity))
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter.attachView(this)
        initView()
    }

    override fun setPresenter(presenter: StandingInstructionContract.NewSIPresenter) {
        this.mNewSIPresenter = presenter
    }

    private fun initView() {
        binding.btnValidTill.setOnClickListener { pickToDate() }
        binding.btnCreateSi.setOnClickListener { createSI() }
        binding.btnConfirm.setOnClickListener { createNewStandingInstruction() }
        binding.btnCancel.setOnClickListener { cancelNewStandingInstruction() }
        binding.btnScanQr.setOnClickListener { scanQrClicked() }
    }

    fun pickToDate() {
        val calendar: Calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month: Int = calendar.get(Calendar.MONTH)
        val year: Int = calendar.get(Calendar.YEAR)
        val picker = DatePickerDialog(
            this, { view, year, monthOfYear, dayOfMonth ->
                binding.btnValidTill.text = "$dayOfMonth-${(monthOfYear + 1)}-$year"
            }, year, month, day
        )
        picker.datePicker.minDate = System.currentTimeMillis()
        picker.show()
    }

    private fun createSI() {
        if (binding.etSiAmount.text.toString() == "") {
            showToast(getString(R.string.enter_amount))
            return
        } else if (binding.etSiAmount.text.toString().toDouble() <= 0) {
            showToast(getString(R.string.enter_valid_amount))
            return
        }
        if (binding.etSiVpa.text.toString() == "") {
            showToast(getString(R.string.enter_VPA))
            return
        }
        if (binding.etSiInterval.text.toString() == "") {
            showToast(getString(R.string.enter_recurrence_interval))
            return
        } else if (binding.etSiInterval.text.toString().toInt() <= 1) {
            showToast(getString(R.string.invalid_recurrence_interval))
            return
        }
        if (binding.btnValidTill.text == Constants.SELECT_DATE) {
            showToast(getString(R.string.select_till_date))
            return
        }
        Utils.hideSoftKeyboard(this)
        mNewSIPresenter.fetchClient(binding.etSiVpa.text.toString())
    }

    override fun showClientDetails(clientId: Long, name: String, externalId: String) {
        this.clientId = clientId
        binding.tvClientName.text = name
        binding.tvClientVpa.text = externalId
        binding.tvAmount.text = resources.getString(
            R.string.currency_amount,
            Constants.RUPEE, binding.etSiAmount.text
        )

        binding.llCreateSi.visibility = View.GONE
        binding.llConfirmTransfer.visibility = View.VISIBLE
        binding.llConfirmCancel.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    fun createNewStandingInstruction() {
        binding.llConfirmCancel.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        mNewSIPresenter.createNewSI(
            clientId, (binding.etSiAmount.text.toString()).toDouble(),
            binding.etSiInterval.text.toString().toInt(), binding.btnValidTill.text.toString()
        )
    }

    fun cancelNewStandingInstruction() {
        binding.llConfirmTransfer.visibility = View.GONE
        binding.llCreateSi.visibility = View.VISIBLE
    }

    fun scanQrClicked() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            val i = Intent(this, ReadQrActivity::class.java)
            startActivityForResult(i, SCAN_QR_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val qrData = data.getStringExtra(Constants.QR_DATA)
            val qrDataArray = qrData?.split(", ")?.toTypedArray()
            if (qrDataArray?.size == 1) {
                binding.etSiVpa.setText(qrDataArray[0])
            } else {
                binding.etSiVpa.setText(qrDataArray?.get(0))
                binding.etSiAmount.setText(qrDataArray?.get(1))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    val i = Intent(this, ReadQrActivity::class.java)
                    startActivityForResult(i, SCAN_QR_REQUEST_CODE)
                } else {
                    Toaster.show(
                        findViewById(android.R.id.content),
                        Constants.NEED_CAMERA_PERMISSION_TO_SCAN_QR_CODE
                    )
                }
            }
        }
    }

    override fun showLoadingView() {
        binding.llCreateSi.visibility = View.GONE
        binding.llConfirmCancel.visibility = View.GONE
        binding.llConfirmTransfer.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun showFailureSearchingClient(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.llCreateSi.visibility = View.VISIBLE
        showToast(message)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        showToast(message)
        setResult(RESULT_OK)
        finish()
    }

    override fun showFailureCreatingNewSI(message: String) {
        binding.llConfirmTransfer.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.llConfirmCancel.visibility = View.VISIBLE
        showToast(message)
    }

}