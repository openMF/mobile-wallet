package org.mifos.mobilewallet.mifospay.bank.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import butterknife.BindView
import butterknife.ButterKnife
import com.alimuzaffar.lib.pin.PinEntryEditText
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.ui.SetupUpiPinActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster

/**
 * Created by ankur on 13/July/2018
 */
class OtpFragment : BaseFragment() {
    @JvmField
    @BindView(R.id.tv_title)
    var mTvTitle: TextView? = null

    @JvmField
    @BindView(R.id.pe_otp)
    var mPeOtp: PinEntryEditText? = null
    private var otp: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity?)?.activityComponent?.inject(this)
        if (arguments != null) {
            otp = arguments?.getString(Constants.OTP)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_otp, container, false) as ViewGroup
        ButterKnife.bind(this, rootView)
        mPeOtp?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                okayClicked()
                return@OnEditorActionListener true
            }
            false
        })
        mPeOtp?.requestFocus()
        return rootView
    }

    fun okayClicked() {
        if (activity is SetupUpiPinActivity) {
            if (mPeOtp?.text.toString() == otp) {
                (activity as SetupUpiPinActivity?)?.otpVerified()
            } else {
                showToast(getString(R.string.wrong_otp))
            }
        }
    }

    fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }

    companion object {
        @JvmStatic
        fun newInstance(otp: String?): OtpFragment {
            val args = Bundle()
            args.putString(Constants.OTP, otp)
            val fragment = OtpFragment()
            fragment.arguments = args
            return fragment
        }
    }
}