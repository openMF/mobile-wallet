package org.mifos.mobilewallet.mifospay.bank.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.BankAccountDetailView
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountDetailPresenter
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

@AndroidEntryPoint
class BankAccountDetailActivity : BaseActivity(), BankAccountDetailView {
    @JvmField
    @Inject
    var mPresenter: BankAccountDetailPresenter? = null
    var mBankAccountDetailPresenter: BankContract.BankAccountDetailPresenter? = null

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.tv_bank_name)
    var mTvBankName: TextView? = null

    @JvmField
    @BindView(R.id.tv_account_holder_name)
    var mTvAccountHolderName: TextView? = null

    @JvmField
    @BindView(R.id.tv_branch)
    var mTvBranch: TextView? = null

    @JvmField
    @BindView(R.id.tv_ifsc)
    var mTvIfsc: TextView? = null

    @JvmField
    @BindView(R.id.tv_type)
    var mTvType: TextView? = null

    @JvmField
    @BindView(R.id.btn_setup_upi_pin)
    var mBtnSetupUpiPin: Button? = null

    @JvmField
    @BindView(R.id.cv_change_upi_pin)
    var mCvChangeUpiPin: CardView? = null

    @JvmField
    @BindView(R.id.cv_forgot_upi_pin)
    var mCvForgotUpiPin: CardView? = null

    @JvmField
    @BindView(R.id.btn_delete_bank)
    var mBtnDeleteBank: Button? = null
    private var bankAccountDetails: BankAccountDetails? = null
    private var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_account_detail)
        ButterKnife.bind(this)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle(Constants.BANK_ACCOUNT_DETAILS)
        mPresenter!!.attachView(this)
        bankAccountDetails = intent.extras!!.getParcelable(Constants.BANK_ACCOUNT_DETAILS)
        index = intent.extras!!.getInt(Constants.INDEX)
        if (bankAccountDetails != null) {
            if (bankAccountDetails!!.isUpiEnabled) {
                mBtnSetupUpiPin!!.visibility = View.GONE
            } else {
                mBtnSetupUpiPin!!.visibility = View.VISIBLE
                mCvChangeUpiPin!!.visibility = View.GONE
                mCvForgotUpiPin!!.visibility = View.GONE
            }
            mTvBankName!!.text = bankAccountDetails!!.bankName
            mTvAccountHolderName!!.text = bankAccountDetails!!.accountholderName
            mTvBranch!!.text = bankAccountDetails!!.branch
            mTvIfsc!!.text = bankAccountDetails!!.ifsc
            mTvType!!.text = bankAccountDetails!!.type
        } else {
            finish()
        }
    }

    @OnClick(R.id.btn_setup_upi_pin)
    fun onSetupUpiPinClicked() {
        startSetupActivity(Constants.SETUP, index)
    }

    @OnClick(R.id.cv_change_upi_pin)
    fun onChangeUpiPinClicked() {
        if (bankAccountDetails!!.isUpiEnabled) {
            startSetupActivity(Constants.CHANGE, index)
        } else {
            showToast(Constants.SETUP_UPI_PIN)
        }
    }

    @OnClick(R.id.cv_forgot_upi_pin)
    fun onForgotUpiPinClicked() {
        if (bankAccountDetails!!.isUpiEnabled) {
            startSetupActivity(Constants.FORGOT, index)
        } else {
            showToast(Constants.SETUP_UPI_PIN)
        }
    }

    private fun startSetupActivity(type: String, index: Int) {
        val intent = Intent(this@BankAccountDetailActivity, SetupUpiPinActivity::class.java)
        intent.putExtra(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails)
        intent.putExtra(Constants.TYPE, type)
        intent.putExtra(Constants.INDEX, index)
        startActivityForResult(intent, SETUP_UPI_REQUEST_CODE)
    }

    fun showToast(message: String?) {
        Toaster.showToast(this, message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DebugUtil.log("rescode ", resultCode)
        if (requestCode == SETUP_UPI_REQUEST_CODE && resultCode == RESULT_OK) {
            val bundle = data!!.extras
            DebugUtil.log("bundle", bundle)
            if (bundle != null) {
                bankAccountDetails = bundle.getParcelable(Constants.UPDATED_BANK_ACCOUNT)
                index = bundle.getInt(Constants.INDEX)
                if (bankAccountDetails!!.isUpiEnabled) {
                    mBtnSetupUpiPin!!.visibility = View.GONE
                    mCvChangeUpiPin!!.visibility = View.VISIBLE
                    mCvForgotUpiPin!!.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constants.UPDATED_BANK_ACCOUNT, bankAccountDetails)
        intent.putExtra(Constants.INDEX, index)
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

    companion object {
        const val SETUP_UPI_REQUEST_CODE = 2
    }

    override fun setPresenter(presenter: BankContract.BankAccountDetailPresenter?) {
        mBankAccountDetailPresenter = presenter
    }
}