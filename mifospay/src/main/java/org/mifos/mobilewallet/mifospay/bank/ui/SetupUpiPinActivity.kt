package org.mifos.mobilewallet.mifospay.bank.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.SetupUpiPinView
import org.mifos.mobilewallet.mifospay.bank.fragment.DebitCardFragment
import org.mifos.mobilewallet.mifospay.bank.fragment.OtpFragment
import org.mifos.mobilewallet.mifospay.bank.fragment.UpiPinFragment
import org.mifos.mobilewallet.mifospay.bank.presenter.SetupUpiPinPresenter
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.utils.AnimationUtil
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

@AndroidEntryPoint
class SetupUpiPinActivity : BaseActivity(), SetupUpiPinView {
    @JvmField
    @Inject
    var mPresenter: SetupUpiPinPresenter? = null
    var mSetupUpiPinPresenter: BankContract.SetupUpiPinPresenter? = null

    @JvmField
    @BindView(R.id.fl_debit_card)
    var mFlDebitCard: FrameLayout? = null

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.fl_otp)
    var mFlOtp: FrameLayout? = null

    @JvmField
    @BindView(R.id.fl_upi_pin)
    var mFlUpiPin: FrameLayout? = null

    @JvmField
    @BindView(R.id.cv_debit_card)
    var mCvDebitCard: CardView? = null

    @JvmField
    @BindView(R.id.tv_debit_card)
    var mTvDebitCard: TextView? = null

    @JvmField
    @BindView(R.id.tv_otp)
    var mTvOtp: TextView? = null

    @JvmField
    @BindView(R.id.tv_upi)
    var mTvUpi: TextView? = null
    private var bankAccountDetails: BankAccountDetails? = null
    private var index = 0
    private var type: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_upi_pin)
        ButterKnife.bind(this)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle(Constants.SETUP_UPI_PIN)
        mPresenter!!.attachView(this)
        val b = intent.extras
        bankAccountDetails = b!!.getParcelable(Constants.BANK_ACCOUNT_DETAILS)
        index = b.getInt(Constants.INDEX)
        type = b.getString(Constants.TYPE)
        if (type == Constants.CHANGE) {
            mSetupUpiPinPresenter!!.requestOtp(bankAccountDetails)
            mFlDebitCard!!.visibility = View.GONE
            mCvDebitCard!!.visibility = View.GONE
            setToolbarTitle(Constants.CHANGE_UPI_PIN)
        } else if (type == Constants.FORGOT) {
            mFlDebitCard!!.visibility = View.VISIBLE
            mCvDebitCard!!.visibility = View.VISIBLE
            setToolbarTitle(Constants.FORGOT_UPI_PIN)
        } else {
            mFlDebitCard!!.visibility = View.VISIBLE
            mCvDebitCard!!.visibility = View.VISIBLE
            setToolbarTitle(Constants.SETUP_UPI_PIN)
        }
        addFragment(DebitCardFragment(), R.id.fl_debit_card)
    }

    override fun debitCardVerified(otp: String?) {
        mTvDebitCard!!.visibility = View.VISIBLE
        addFragment(OtpFragment.newInstance(otp), R.id.fl_otp)
        mFlDebitCard?.let { AnimationUtil.collapse(it) }
        mFlOtp?.let { AnimationUtil.expand(it) }
    }

    fun otpVerified() {
        mTvOtp!!.visibility = View.VISIBLE
        addFragment(UpiPinFragment(), R.id.fl_upi_pin)
        mFlUpiPin?.let { AnimationUtil.expand(it) }
        mFlOtp?.let { AnimationUtil.collapse(it) }
    }

    fun upiPinEntered(upiPin: String?) {
        replaceFragment(UpiPinFragment.newInstance(1, upiPin), false, R.id.fl_upi_pin)
    }

    fun upiPinConfirmed(upiPin: String?) {
        mTvUpi!!.visibility = View.VISIBLE
        showProgressDialog(Constants.SETTING_UP_UPI_PIN)
        mSetupUpiPinPresenter!!.setupUpiPin(bankAccountDetails, upiPin)
    }

    override fun setupUpiPinSuccess(mSetupUpiPin: String?) {
        bankAccountDetails!!.isUpiEnabled = true
        bankAccountDetails!!.upiPin = mSetupUpiPin
        hideProgressDialog()
        showToast(Constants.UPI_PIN_SETUP_COMPLETED_SUCCESSFULLY)
        val intent = Intent()
        intent.putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails)
        intent.putExtra(Constants.INDEX, index)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun setupUpiPinError(message: String?) {
        hideProgressDialog()
        showToast(Constants.ERROR_WHILE_SETTING_UP_UPI_PIN)
    }

    override fun setPresenter(presenter: BankContract.SetupUpiPinPresenter?) {
        mSetupUpiPinPresenter = presenter
    }

    fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }
}