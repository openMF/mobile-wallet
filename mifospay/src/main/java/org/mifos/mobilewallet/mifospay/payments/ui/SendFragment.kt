package org.mifos.mobilewallet.mifospay.payments.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.transition.TransitionManager
import android.support.v4.content.ContextCompat
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment
import org.mifos.mobilewallet.mifospay.databinding.FragmentSendBinding
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils

/**
 * Created by naman on 30/8/17.
 */
class SendFragment : BaseFragment(), BaseHomeContract.TransferView {

    private lateinit var mPresenter: TransferPresenter
    private lateinit var mTransferPresenter: BaseHomeContract.TransferPresenter
    private lateinit var vpa: String
    private var _binding: FragmentSendBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity?)?.activityComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendBinding.inflate(inflater, container, false)
        setSwipeEnabled(false)
        mPresenter.attachView(this)
        _binding?.etMobileNumber?.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        _binding?.btnVpa?.isSelected = true


        _binding?.btnVpa?.setOnClickListener {
            onVPASelected()
        }

        _binding?.btnMobile?.setOnClickListener {
            onMobileSelected()
        }

        _binding?.btnSubmit?.setOnClickListener {
            transferClicked()
        }

        _binding?.btnScanQr?.setOnClickListener {
            scanQrClicked()
        }

        _binding?.btnSearchContact?.setOnClickListener {
            searchContactClicked()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onVPASelected() {
        _binding?.rlSendContainer?.let { TransitionManager.beginDelayedTransition(it) }
        _binding?.btnVpa?.isSelected = true
        _binding?.btnVpa?.isFocusable = true
        _binding?.btnVpa?.setChipBackgroundColorResource(R.color.clickedblue)
        _binding?.btnMobile?.isSelected = false
        _binding?.btnMobile?.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        _binding?.btnScanQr?.visibility = View.VISIBLE
        _binding?.rlMobile?.visibility = View.GONE
        _binding?.tilVpa?.visibility = View.VISIBLE
        activity?.let { Utils.hideSoftKeyboard(it) }
    }

    fun onMobileSelected() {
        _binding?.rlSendContainer?.let { TransitionManager.beginDelayedTransition(it) }
        _binding?.btnMobile?.isSelected = true
        _binding?.btnMobile?.isFocusable = true
        _binding?.btnMobile?.setChipBackgroundColorResource(R.color.clickedblue)
        _binding?.btnVpa?.isSelected = false
        _binding?.btnVpa?.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        _binding?.tilVpa?.visibility = View.GONE
        _binding?.btnScanQr?.visibility = View.GONE
        _binding?.rlMobile?.visibility = View.VISIBLE
        activity?.let { Utils.hideSoftKeyboard(it) }
    }

    fun transferClicked() {
        val externalId = _binding?.etVpa?.text.toString().trim { it <= ' ' }
        val eamount = _binding?.etAmount?.text.toString().trim { it <= ' ' }
        val mobileNumber = _binding?.etMobileNumber?.text
            .toString().trim { it <= ' ' }.replace("\\s+".toRegex(), "")
        if (eamount == "" || _binding?.btnVpa?.isSelected == true && externalId == "" ||
            _binding?.btnMobile?.isSelected == true && mobileNumber == ""
        ) {
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
            if (externalId != Constants.VPA_VALIDATION_REGEX) {
                showSnackbar(getString(R.string.please_enter_valid_vpa))
                return
            }
            if (!mTransferPresenter.checkSelfTransfer(externalId)) {
                mTransferPresenter.checkBalanceAvailability(externalId, amount)
            } else {
                showSnackbar(Constants.SELF_ACCOUNT_ERROR)
            }
        }
    }

    fun scanQrClicked() {
        if (Build.VERSION.SDK_INT >= 23 && activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {

            // Permission has already been granted
            val i = Intent(activity, ReadQrActivity::class.java)
            startActivityForResult(i, SCAN_QR_REQUEST_CODE)
        }
    }

    override fun showVpa(vpa: String) {
        this.vpa = vpa
    }

    override fun setPresenter(presenter: BaseHomeContract.TransferPresenter) {
        mTransferPresenter = presenter
    }

    fun searchContactClicked() {
        if (Build.VERSION.SDK_INT >= 23 && activity?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                )
            } != PackageManager.PERMISSION_GRANTED
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val qrData = data.getStringExtra(Constants.QR_DATA)
            val qrDataArray = qrData?.split(", ".toRegex())?.toTypedArray()
            if (qrDataArray != null) {
                if (qrDataArray.size == 1) {
                    _binding?.etVpa?.setText(qrDataArray[0])
                } else {
                    _binding?.etVpa?.setText(qrDataArray[0])
                    _binding?.etAmount?.setText(qrDataArray[1])
                }
            }
            val externalId = _binding?.etVpa?.text.toString()
            if (_binding?.etAmount?.text.toString().isEmpty()) {
                showSnackbar(Constants.PLEASE_ENTER_AMOUNT)
                return
            }
            val amount = _binding?.etAmount?.text.toString().toDouble()
            if (!mTransferPresenter.checkSelfTransfer(externalId)) {
                mTransferPresenter.checkBalanceAvailability(externalId, amount)
            } else {
                showSnackbar(Constants.SELF_ACCOUNT_ERROR)
            }
        } else if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            val cursor: Cursor?
            try {
                val phoneNo: String?
                val name: String?
                // getData() method will have the Content Uri of the selected contact
                val uri = data.data
                //Query the content uri
                cursor = uri?.let { context?.contentResolver?.query(it, null, null, null, null) }
                cursor?.moveToFirst()
                // column index of the phone number
                val phoneIndex = cursor?.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                // column index of the contact name
                val nameIndex = cursor?.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )
                phoneNo = phoneIndex?.let { cursor.getString(it) }
                name = nameIndex?.let { cursor.getString(it) }
                _binding?.etMobileNumber?.setText(phoneNo)
            } catch (e: Exception) {
                showToast(Constants.ERROR_CHOOSING_CONTACT)
            }
        } else if (requestCode == REQUEST_SHOW_DETAILS && resultCode == Activity.RESULT_CANCELED) {
            if (_binding?.btnMobile?.isSelected == true) {
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

    override fun showToast(message: String) {
        Toaster.showToast(context, message)
    }

    override fun showSnackbar(message: String) {
        Toaster.show(view, message)
    }

    override fun showMobile(mobileNo: String) {}
    override fun showClientDetails(externalId: String, amount: Double) {
        val fragment = MakeTransferFragment.newInstance(externalId, amount)
        fragment.setTargetFragment(this, REQUEST_SHOW_DETAILS)
        if (parentFragment != null) {
            fragment.show(
                parentFragment?.childFragmentManager,
                Constants.MAKE_TRANSFER_FRAGMENT
            )
        }
    }

    companion object {
        const val REQUEST_SHOW_DETAILS = 3
        private const val REQUEST_CAMERA = 0
        private const val SCAN_QR_REQUEST_CODE = 666
        private const val PICK_CONTACT = 1
        private const val REQUEST_READ_CONTACTS = 2
    }
}