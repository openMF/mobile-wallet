package org.mifos.mobilewallet.mifospay.payments.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment.Companion.newInstance
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.hideSoftKeyboard
import javax.inject.Inject

/**
 * Created by naman on 30/8/17.
 */
@AndroidEntryPoint
class SendFragment : BaseFragment(), BaseHomeContract.TransferView {
    @JvmField
    @Inject
    var mPresenter: TransferPresenter? = null
    var mTransferPresenter: BaseHomeContract.TransferPresenter? = null

    @JvmField
    @BindView(R.id.rl_send_container)
    var sendContainer: ViewGroup? = null

    @JvmField
    @BindView(R.id.et_amount)
    var etAmount: EditText? = null

    @JvmField
    @BindView(R.id.et_vpa)
    var etVpa: EditText? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var btnTransfer: Button? = null

    @JvmField
    @BindView(R.id.btn_scan_qr)
    var btnScanQr: TextView? = null

    @JvmField
    @BindView(R.id.btn_vpa)
    var mBtnVpa: Chip? = null

    @JvmField
    @BindView(R.id.btn_mobile)
    var mBtnMobile: Chip? = null

    @JvmField
    @BindView(R.id.et_mobile_number)
    var mEtMobileNumber: EditText? = null

    @JvmField
    @BindView(R.id.btn_search_contact)
    var mBtnSearchContact: TextView? = null

    @JvmField
    @BindView(R.id.rl_mobile)
    var mRlMobile: RelativeLayout? = null

    @JvmField
    @BindView(R.id.til_vpa)
    var mTilVpa: TextInputLayout? = null
    private var vpa: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_send, container,
            false
        )
        ButterKnife.bind(this, rootView)
        setSwipeEnabled(false)
        mPresenter!!.attachView(this)
        mEtMobileNumber!!.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        mBtnVpa!!.isSelected = true
        return rootView
    }

    @OnClick(R.id.btn_vpa)
    fun onVPASelected() {
        TransitionManager.beginDelayedTransition(sendContainer!!)
        mBtnVpa!!.isSelected = true
        mBtnVpa!!.isFocusable = true
        mBtnVpa!!.setChipBackgroundColorResource(R.color.black)
        mBtnVpa!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        mBtnMobile!!.isSelected = false
        mBtnMobile!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        mBtnMobile!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        btnScanQr!!.visibility = View.VISIBLE
        mRlMobile!!.visibility = View.GONE
        mTilVpa!!.visibility = View.VISIBLE
        hideSoftKeyboard(activity!!)
    }

    @OnClick(R.id.btn_mobile)
    fun onMobileSelected() {
        TransitionManager.beginDelayedTransition(sendContainer!!)
        mBtnMobile!!.isSelected = true
        mBtnMobile!!.isFocusable = true
        mBtnMobile!!.setChipBackgroundColorResource(R.color.black)
        mBtnMobile!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        mBtnVpa!!.isSelected = false
        mBtnVpa!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        mBtnVpa!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        mTilVpa!!.visibility = View.GONE
        btnScanQr!!.visibility = View.GONE
        mRlMobile!!.visibility = View.VISIBLE
        hideSoftKeyboard(activity!!)
    }

    @OnClick(R.id.btn_submit)
    fun transferClicked() {
        val externalId = etVpa!!.text.toString().trim { it <= ' ' }
        val eamount = etAmount!!.text.toString().trim { it <= ' ' }
        val mobileNumber = mEtMobileNumber!!.text
            .toString().trim { it <= ' ' }.replace("\\s+".toRegex(), "")
        if (eamount == "" || mBtnVpa!!.isSelected && externalId == "" || mBtnMobile!!.isSelected && mobileNumber == "") {
            Toast.makeText(
                activity,
                Constants.PLEASE_ENTER_ALL_THE_FIELDS, Toast.LENGTH_SHORT
            ).show()
        } else {
            val amount = eamount.toDouble()
            if (amount <= 0) {
                showSnackbar(Constants.PLEASE_ENTER_VALID_AMOUNT)
                return
            }
            if (!externalId.matches(Constants.VPA_VALIDATION_REGEX.toRegex())) {
                showSnackbar(getString(R.string.please_enter_valid_vpa))
                return
            }
            if (!mTransferPresenter!!.checkSelfTransfer(externalId)) {
                mTransferPresenter!!.checkBalanceAvailability(externalId, amount)
            } else {
                showSnackbar(Constants.SELF_ACCOUNT_ERROR)
            }
        }
    }

    @OnClick(R.id.btn_scan_qr)
    fun scanQrClicked() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {

            // Permission has already been granted
            val i = Intent(activity, ReadQrActivity::class.java)
            startActivityForResult(i, SCAN_QR_REQUEST_CODE)
        }
    }

    override fun showVpa(vpa: String?) {
        this.vpa = vpa
    }

    @OnClick(R.id.btn_search_contact)
    fun searchContactClicked() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {

            // Permission has already been granted
            val intent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            startActivityForResult(intent, PICK_CONTACT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val qrData = data!!.getStringExtra(Constants.QR_DATA)
            val qrDataArray =
                qrData!!.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (qrDataArray.size == 1) {
                etVpa!!.setText(qrDataArray[0])
            } else {
                etVpa!!.setText(qrDataArray[0])
                etAmount!!.setText(qrDataArray[1])
            }
            val externalId = etVpa!!.text.toString().trim { it <= ' ' }
            if (etAmount!!.text.toString().isEmpty()) {
                showSnackbar(Constants.PLEASE_ENTER_AMOUNT)
                return
            }
            val amount = etAmount!!.text.toString().toDouble()
            if (!mTransferPresenter!!.checkSelfTransfer(externalId)) {
                mTransferPresenter!!.checkBalanceAvailability(externalId, amount)
            } else {
                showSnackbar(Constants.SELF_ACCOUNT_ERROR)
            }
        } else if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            var cursor: Cursor? = null
            try {
                var phoneNo: String? = null
                var name: String? = null
                // getData() method will have the Content Uri of the selected contact
                val uri = data!!.data
                //Query the content uri
                cursor = requireContext().contentResolver.query(uri!!, null, null, null, null)
                cursor!!.moveToFirst()
                // column index of the phone number
                val phoneIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                // column index of the contact name
                val nameIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )
                phoneNo = cursor.getString(phoneIndex)
                name = cursor.getString(nameIndex)
                mEtMobileNumber!!.setText(phoneNo)
            } catch (e: Exception) {
                showToast(Constants.ERROR_CHOOSING_CONTACT)
            }
        } else if (requestCode == REQUEST_SHOW_DETAILS && resultCode == Activity.RESULT_CANCELED) {
            if (mBtnMobile!!.isSelected) {
                showSnackbar(Constants.ERROR_FINDING_MOBILE_NUMBER)
            } else {
                showSnackbar(Constants.ERROR_FINDING_VPA)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.
                    val i = Intent(activity, ReadQrActivity::class.java)
                    startActivityForResult(i, SCAN_QR_REQUEST_CODE)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toaster.show(view, Constants.NEED_CAMERA_PERMISSION_TO_SCAN_QR_CODE)
                }
                return
            }

            REQUEST_READ_CONTACTS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    )
                    startActivityForResult(intent, PICK_CONTACT)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toaster.show(view, Constants.NEED_READ_CONTACTS_PERMISSION)
                }
                return
            }
        }
    }

    override fun showToast(message: String?) {
        Toaster.showToast(context, message)
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(view, message)
    }

    override fun showMobile(mobileNo: String?) {}
    override fun showClientDetails(externalId: String?, amount: Double) {
        val fragment = newInstance(externalId, amount)
        fragment.setTargetFragment(this, REQUEST_SHOW_DETAILS)
        if (parentFragment != null) {
            fragment.show(
                requireParentFragment().childFragmentManager,
                Constants.MAKE_TRANSFER_FRAGMENT
            )
        }
    }

    override fun setPresenter(presenter: BaseHomeContract.TransferPresenter?) {
        mTransferPresenter = presenter
    }

    companion object {
        const val REQUEST_SHOW_DETAILS = 3
        private const val REQUEST_CAMERA = 0
        private const val SCAN_QR_REQUEST_CODE = 666
        private const val PICK_CONTACT = 1
        private const val REQUEST_READ_CONTACTS = 2
    }
}