package org.mifos.mobilewallet.mifospay.merchants.ui

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.textfield.TextInputEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.mifospay.MifosPayApp
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.common.ui.MakeTransferFragment
import org.mifos.mobilewallet.mifospay.history.ui.adapter.SpecificTransactionsAdapter
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.MerchantTransferView
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantTransferPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.TextDrawable
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

/**
 * Created by Shivansh Tiwari on 06/07/19.
 */
@AndroidEntryPoint
class MerchantTransferActivity : BaseActivity(), MerchantTransferView {
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null

    @JvmField
    @BindView(R.id.nsv_merchant_bottom_sheet_dialog)
    var vMerchantBottomSheetDialog: View? = null

    @JvmField
    @BindView(R.id.iv_merchant_image)
    var ivMerchantImage: ImageView? = null

    @JvmField
    @BindView(R.id.tv_pay_to_name)
    var tvMerchantName: TextView? = null

    @JvmField
    @BindView(R.id.tv_pay_to_vpa)
    var tvMerchantVPA: TextView? = null

    @JvmField
    @BindView(R.id.et_merchant_amount)
    var etAmount: TextInputEditText? = null

    @JvmField
    @BindView(R.id.btn_submit)
    var btnSubmit: Button? = null

    @JvmField
    @BindView(R.id.rv_merchant_history)
    var rvMerchantHistory: RecyclerView? = null

    @JvmField
    @BindView(R.id.inc_empty_transactions_state_view)
    var vEmptyState: View? = null

    @JvmField
    @BindView(R.id.iv_empty_no_transaction_history)
    var ivTransactionsStateIcon: ImageView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_title)
    var tvTransactionsStateTitle: TextView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    var tvTransactionsStateSubtitle: TextView? = null

    @JvmField
    @Inject
    var mPresenter: MerchantTransferPresenter? = null
    var mTransferPresenter: BaseHomeContract.MerchantTransferPresenter? = null
    private var merchantAccountNumber: String? = null

    @JvmField
    @Inject
    var mMerchantHistoryAdapter: SpecificTransactionsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_transaction)
        ButterKnife.bind(this)
        setToolbarTitle("Merchant Transaction")
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setupUI()
        mPresenter?.attachView(this)
        mPresenter?.fetchMerchantTransfers(merchantAccountNumber)
    }

    private fun setupUI() {
        setupBottomSheet()
        merchantAccountNumber = intent.getStringExtra(Constants.MERCHANT_ACCOUNT_NO)
        tvMerchantName?.text =
            intent.getStringExtra(Constants.MERCHANT_NAME)
        tvMerchantVPA?.text = intent.getStringExtra(Constants.MERCHANT_VPA)
        val drawable = TextDrawable.builder().beginConfig()
            .width(resources.getDimension(R.dimen.user_profile_image_size).toInt())
            .height(resources.getDimension(R.dimen.user_profile_image_size).toInt())
            .endConfig().buildRound(
                intent.getStringExtra(Constants.MERCHANT_NAME)
                    ?.substring(0, 1), R.color.colorPrimary
            )
        ivMerchantImage?.setImageDrawable(drawable)
        showTransactionFetching()
        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        mMerchantHistoryAdapter?.setContext(this)
        rvMerchantHistory?.layoutManager =
            LinearLayoutManager(MifosPayApp.context)
        rvMerchantHistory?.adapter = mMerchantHistoryAdapter
    }

    override fun setPresenter(presenter: BaseHomeContract.MerchantTransferPresenter?) {
        mTransferPresenter = presenter
    }

    private fun setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(vMerchantBottomSheetDialog!!)
        mBottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    else -> {}
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })
    }

    @OnClick(R.id.btn_submit)
    fun makeTransaction() {
        val externalId = tvMerchantVPA?.text.toString().trim { it <= ' ' }
        val amount = etAmount?.text.toString().trim { it <= ' ' }
        if (amount.isEmpty()) {
            showToast(Constants.PLEASE_ENTER_ALL_THE_FIELDS)
            return
        } else if (amount.toDouble() <= 0) {
            showToast(Constants.PLEASE_ENTER_VALID_AMOUNT)
            return
        }
        mTransferPresenter?.checkBalanceAvailability(externalId, amount.toDouble())
    }

    override fun onBackPressed() {
        if (mBottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }
        super.onBackPressed()
    }

    override fun showToast(message: String?) {
        Toaster.showToast(MifosPayApp.context, message)
    }

    override fun showPaymentDetails(externalId: String?, amount: Double) {
        val fragment = MakeTransferFragment.newInstance(externalId, amount)
        fragment.show(supportFragmentManager, "tag")
    }

    override fun showTransactionFetching() {
        rvMerchantHistory?.visibility = View.GONE
        tvTransactionsStateTitle?.text = resources.getString(R.string.fetching)
        tvTransactionsStateSubtitle?.visibility = View.GONE
        ivTransactionsStateIcon?.visibility = View.GONE
    }

    override fun showTransactions(transactions: List<Transaction?>?) {
        vEmptyState?.visibility = View.GONE
        rvMerchantHistory?.visibility = View.VISIBLE
        mMerchantHistoryAdapter?.setData(transactions as List<Transaction>)
    }

    override fun showSpecificView(drawable: Int, title: Int, subtitle: Int) {
        rvMerchantHistory?.visibility = View.GONE
        tvTransactionsStateSubtitle?.visibility = View.VISIBLE
        ivTransactionsStateIcon?.visibility = View.VISIBLE
        tvTransactionsStateTitle?.setText(title)
        tvTransactionsStateSubtitle?.setText(subtitle)
        ivTransactionsStateIcon?.setImageDrawable(resources.getDrawable(drawable))
    }
}