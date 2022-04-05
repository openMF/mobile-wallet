package org.mifos.mobilewallet.mifospay.bank.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import butterknife.BindView
import butterknife.ButterKnife
import com.alimuzaffar.lib.pin.PinEntryEditText
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.DebitCardView
import org.mifos.mobilewallet.mifospay.bank.presenter.DebitCardPresenter
import org.mifos.mobilewallet.mifospay.bank.ui.SetupUpiPinActivity
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by ankur on 13/July/2018
 */
class DebitCardFragment : BaseFragment(), DebitCardView {
    @JvmField
    @Inject
    var mPresenter: DebitCardPresenter? = null
    private var mDebitCardPresenter: BankContract.DebitCardPresenter? = null

    @JvmField
    @BindView(R.id.et_debit_card_number)
    var mEtDebitCardNumber: EditText? = null

    @JvmField
    @BindView(R.id.pe_month)
    var mPeMonth: PinEntryEditText? = null

    @JvmField
    @BindView(R.id.pe_year)
    var mPeYear: PinEntryEditText? = null
    private var key = false
    override fun setPresenter(presenter: BankContract.DebitCardPresenter?) {
        mDebitCardPresenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity?)!!.activityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(
            R.layout.fragment_debit_card,
            container, false
        ) as ViewGroup
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        mPeYear!!.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                okayClicked()
                return@OnEditorActionListener true
            }
            false
        })
        mPeMonth!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 2) {
                    mPeYear!!.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mPeYear!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && mPeYear!!.length() == 0) {
                if (key) {
                    mPeMonth!!.requestFocus()
                    mPeMonth!!.dispatchKeyEvent(
                        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
                    )
                    key = false
                    return@OnKeyListener false
                }
                key = true
            }
            false
        })
        return rootView
    }

    private fun okayClicked() {
        showProgressDialog(Constants.PLEASE_WAIT)
        mDebitCardPresenter!!.verifyDebitCard(
            mEtDebitCardNumber!!.text
                .toString(), mPeMonth!!.text.toString(), mPeYear!!.text.toString()
        )
    }

    override fun verifyDebitCardSuccess(otp: String?) {
        hideProgressDialog()
        if (activity is SetupUpiPinActivity) {
            (activity as SetupUpiPinActivity?)!!.debitCardVerified(otp)
        }
    }

    override fun verifyDebitCardError(message: String?) {
        hideProgressDialog()
        mEtDebitCardNumber!!.requestFocusFromTouch()
        showToast(message)
    }

    fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }
}