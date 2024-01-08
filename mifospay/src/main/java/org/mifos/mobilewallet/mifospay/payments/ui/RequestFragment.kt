package org.mifos.mobilewallet.mifospay.payments.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferPresenter
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class RequestFragment : BaseFragment(), BaseHomeContract.TransferView {
    @JvmField
    @Inject
    var mPresenter: TransferPresenter? = null
    var mTransferPresenter: BaseHomeContract.TransferPresenter? = null

    @JvmField
    @BindView(R.id.tv_client_mobile)
    var mTvClientMobile: TextView? = null

    @JvmField
    @BindView(R.id.tv_client_vpa)
    var tvClientVpa: TextView? = null

    @JvmField
    @BindView(R.id.btn_show_qr)
    var btnShowQr: TextView? = null
    private var vpa: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_request, container, false)
        ButterKnife.bind(this, root)
        mPresenter!!.attachView(this)
        mPresenter!!.fetchVpa()
        mPresenter!!.fetchMobile()
        return root
    }

    @OnClick(R.id.btn_show_qr)
    fun showQrClicked() {
        val intent = Intent(activity, ShowQrActivity::class.java)
        intent.putExtra(Constants.QR_DATA, vpa)
        startActivity(intent)
    }

    override fun showVpa(vpa: String?) {
        this.vpa = vpa
        tvClientVpa!!.text = vpa
        btnShowQr!!.isClickable = true
    }

    override fun showMobile(mobileNo: String?) {
        val phoneNumberUtil = PhoneNumberUtil.createInstance(mTvClientMobile!!.context)
        try {
            val phoneNumber = phoneNumberUtil.parse(mobileNo, Locale.getDefault().country)
            mTvClientMobile!!.text = phoneNumberUtil.format(
                phoneNumber,
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            )
        } catch (e: NumberParseException) {
            mTvClientMobile!!.text = mobileNo // If mobile number is not parsed properly
        }
    }

    override fun showClientDetails(externalId: String?, amount: Double) {}

    override fun setPresenter(presenter: BaseHomeContract.TransferPresenter?) {
        mTransferPresenter = presenter
    }

    override fun showToast(message: String?) {
        Toaster.showToast(context, message)
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(view, message)
    }
}