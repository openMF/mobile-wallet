package org.mifos.mobilewallet.mifospay.payments.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.databinding.FragmentRequestBinding
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import java.util.*

class RequestFragment : BaseFragment(), BaseHomeContract.TransferView {
    private lateinit var mPresenter: TransferPresenter
    private lateinit var mTransferPresenter: BaseHomeContract.TransferPresenter
    private lateinit var mTvClientMobile: TextView
    private lateinit var tvClientVpa: TextView
    private lateinit var btnShowQr: TextView
    private lateinit var vpa: String
    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity?)?.activityComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        mPresenter.attachView(this)
        mPresenter.fetchVpa()
        mPresenter.fetchMobile()

        _binding?.btnShowQr?.setOnClickListener {
            showQrClicked()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setPresenter(presenter: BaseHomeContract.TransferPresenter) {
        mTransferPresenter = presenter
    }

    fun showQrClicked() {
        val intent = Intent(activity, ShowQrActivity::class.java)
        intent.putExtra(Constants.QR_DATA, vpa)
        startActivity(intent)
    }

    override fun showVpa(vpa: String) {
        this.vpa = vpa
        tvClientVpa.text = vpa
        btnShowQr.isClickable = true
    }

    override fun showMobile(mobileNo: String) {
        val phoneNumberUtil = PhoneNumberUtil.createInstance(mTvClientMobile.context)
        try {
            val phoneNumber = phoneNumberUtil.parse(mobileNo, Locale.getDefault().country)
            mTvClientMobile.text = phoneNumberUtil.format(
                phoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            )
        } catch (e: NumberParseException) {
            mTvClientMobile.text = mobileNo // If mobile number is not parsed properly
        }
    }

    override fun showClientDetails(externalId: String, amount: Double) {}
    override fun showToast(message: String) {
        Toaster.showToast(context, message)
    }

    override fun showSnackbar(message: String) {
        Toaster.show(view, message)
    }
}