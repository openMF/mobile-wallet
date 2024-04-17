package org.mifospay.bank.fragment

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
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.R
import org.mifospay.bank.BankContract
import org.mifospay.bank.BankContract.DebitCardView
import org.mifospay.bank.presenter.DebitCardPresenter
import org.mifospay.bank.ui.SetupUpiPinActivity
import org.mifospay.base.BaseFragment
import org.mifospay.common.Constants
import org.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by ankur on 13/July/2018
 */
@AndroidEntryPoint
class DebitCardFragment : BaseFragment(), DebitCardView {
    @JvmField
    @Inject
    var mPresenter: DebitCardPresenter? = null
    var mDebitCardPresenter: BankContract.DebitCardPresenter? = null

    @JvmField
    @BindView(R.id.et_debit_card_number)
    var mEtDebitCardNumber: EditText? = null

    @JvmField
    @BindView(R.id.pe_month)
    var mPeMonth: PinEntryEditText? = null

    @JvmField
    @BindView(R.id.pe_year)
    var mPeYear: PinEntryEditText? = null
    var key = false

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
        mDebitCardPresenter!!.verifyDebitCard(mEtDebitCardNumber!!.text
            .toString().trim { it <= ' ' },
            mPeMonth!!.text.toString(),
            mPeYear!!.text.toString()
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

    override fun setPresenter(presenter: BankContract.DebitCardPresenter?) {
        mDebitCardPresenter = presenter
    }

    fun showToast(message: String?) {
        Toaster.showToast(activity, message)
    }
}