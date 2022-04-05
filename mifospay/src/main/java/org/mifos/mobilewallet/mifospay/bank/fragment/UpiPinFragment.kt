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
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.UpiPinView
import org.mifos.mobilewallet.mifospay.bank.presenter.UpiPinPresenter
import org.mifos.mobilewallet.mifospay.bank.ui.SetupUpiPinActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by ankur on 13/July/2018
 */
class UpiPinFragment : BaseFragment(), UpiPinView {
    @JvmField
    @Inject
    var mPresenter: UpiPinPresenter? = null
    private var mUpiPinPresenter: BankContract.UpiPinPresenter? = null

    @JvmField
    @BindView(R.id.tv_title)
    var mTvTitle: TextView? = null

    @JvmField
    @BindView(R.id.pe_upi_pin)
    var mPeUpiPin: PinEntryEditText? = null
    private var step = 0
    private var upiPin: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity?)!!.activityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(
            R.layout.fragment_upi_pin_setup,
            container, false
        ) as ViewGroup
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        val b = arguments
        if (b != null) {
            step = b.getInt(Constants.STEP, 0)
            upiPin = b.getString(Constants.UPI_PIN, null)
            mTvTitle!!.setText(R.string.reenter_upi)
        }
        mPeUpiPin!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                okayClicked()
                return@OnEditorActionListener true
            }
            false
        })
        mPeUpiPin!!.requestFocus()
        return rootView
    }

    private fun okayClicked() {
        if (activity is SetupUpiPinActivity) {
            if (mPeUpiPin!!.text.toString().length == 4) {
                if (step == 1) {
                    if (upiPin == mPeUpiPin!!.text.toString()) {
                        (activity as SetupUpiPinActivity?)!!.upiPinConfirmed(upiPin)
                    } else {
                        showToast(getString(R.string.upi_pin_mismatch))
                    }
                } else {
                    (activity as SetupUpiPinActivity?)!!.upiPinEntered(
                        mPeUpiPin!!.text.toString()
                    )
                }
            } else {
                showToast(getString(R.string.enter_upi_length_4))
            }
        }
    }

    fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }

    override fun setPresenter(presenter: BankContract.UpiPinPresenter?) {
        mUpiPinPresenter = presenter
    }

    companion object {
        @JvmStatic
        fun newInstance(step: Int, upiPin: String?): UpiPinFragment {
            val args = Bundle()
            args.putInt(Constants.STEP, step)
            args.putString(Constants.UPI_PIN, upiPin)
            val fragment = UpiPinFragment()
            fragment.arguments = args
            return fragment
        }
    }
}