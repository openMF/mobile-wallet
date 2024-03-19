package org.mifos.mobilewallet.mifospay.home.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter
import org.mifos.mobilewallet.mifospay.qr.ui.ReadQrActivity
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.NewSIActivity
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster

@AndroidEntryPoint
class PaymentsFragment : BaseFragment() {

    private val transferPresenter: TransferPresenter by viewModels()
    private val newSIActivityRequestCode = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupUi()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    PaymentsScreen(
                        showQr = { showQrClicked() },
                        searchContact = { searchContactClicked() },
                        scanQr = { scanQrClicked() },
                        onNewSI = { onNewSI() }
                    )
                }
            }
        }
    }

    private fun setupUi() {
        setSwipeEnabled(false)
        setToolbarTitle(getString(R.string.payments))
    }

    private fun showQrClicked() {
        val intent = Intent(activity, ShowQrActivity::class.java)
        intent.putExtra(Constants.QR_DATA, transferPresenter.vpa.value)
        startActivity(intent)
    }

    private fun searchContactClicked() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {
            val intent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            )
            startActivityForResult(intent, PICK_CONTACT)
        }
    }

    private fun scanQrClicked() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            val i = Intent(activity, ReadQrActivity::class.java)
            startActivityForResult(i, SCAN_QR_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SCAN_QR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val qrData = data!!.getStringExtra(Constants.QR_DATA)
            val qrDataArray =
                qrData!!.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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
            } catch (e: Exception) {
                Toaster.show(view, Constants.ERROR_CHOOSING_CONTACT)
            }
        } else if (requestCode == REQUEST_SHOW_DETAILS && resultCode == Activity.RESULT_CANCELED) {
            Toaster.show(view, Constants.ERROR_FINDING_VPA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val i = Intent(activity, ReadQrActivity::class.java)
                    startActivityForResult(i, SCAN_QR_REQUEST_CODE)
                } else {
                    Toaster.show(view, Constants.NEED_CAMERA_PERMISSION_TO_SCAN_QR_CODE)
                }
                return
            }

            REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    )
                    startActivityForResult(intent, PICK_CONTACT)
                } else {
                    Toaster.show(view, Constants.NEED_READ_CONTACTS_PERMISSION)
                }
                return
            }
        }
    }

    private fun onNewSI() {
        val i = Intent(activity, NewSIActivity::class.java)
        startActivityForResult(i, newSIActivityRequestCode)
    }

    companion object {
        fun newInstance(): PaymentsFragment {
            val args = Bundle()
            val fragment = PaymentsFragment()
            fragment.arguments = args
            return fragment
        }

        const val REQUEST_SHOW_DETAILS = 3
        private const val REQUEST_CAMERA = 0
        private const val SCAN_QR_REQUEST_CODE = 666
        private const val PICK_CONTACT = 1
        private const val REQUEST_READ_CONTACTS = 2
    }
}