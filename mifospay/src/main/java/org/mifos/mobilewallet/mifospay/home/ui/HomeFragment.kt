package org.mifos.mobilewallet.mifospay.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.Account
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.databinding.FragmentHomeBinding
import org.mifos.mobilewallet.mifospay.history.ui.adapter.HistoryAdapter
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract
import org.mifos.mobilewallet.mifospay.home.BaseHomeContract.HomeView
import org.mifos.mobilewallet.mifospay.home.presenter.HomeViewModel
import org.mifos.mobilewallet.mifospay.payments.ui.SendActivity
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.Toaster
import org.mifos.mobilewallet.mifospay.utils.Utils.getFormattedAccountBalance
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment(), HomeView {

    private val homeViewModel: HomeViewModel by viewModels()

    private var mHomePresenter: BaseHomeContract.HomePresenter? = null

    @JvmField
    @Inject
    var mHistoryAdapter: HistoryAdapter? = null

    @JvmField
    @BindView(R.id.tv_account_balance)
    var mTvAccountBalance: TextView? = null

    @JvmField
    @BindView(R.id.nsv_home_bottom_sheet_dialog)
    var vHomeBottomSheetDialog: View? = null

    @JvmField
    @BindView(R.id.rv_home_bottom_sheet)
    var rvHomeBottomSheetContent: RecyclerView? = null

    @JvmField
    @BindView(R.id.btn_home_bottom_sheet_action)
    var btnShowMoreTransactionsHistory: Button? = null

    @JvmField
    @BindView(R.id.inc_empty_transactions_state_view)
    var vStateView: View? = null

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
    @BindView(R.id.tv_hide_balance)
    var tvHideBalance: TextView? = null

    @JvmField
    @BindView(R.id.cc_home_screen)
    var homeScreenContainer: ViewGroup? = null

    @JvmField
    @BindView(R.id.tv_loading_history)
    var tvLoadingTransactions: TextView? = null

    @JvmField
    @BindView(R.id.pb_loading_history)
    var progressBar: ProgressBar? = null

    private var account: Account? = null
    private var accountBalance: String? = null

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setToolbarTitle(Constants.HOME)
        ButterKnife.bind(this, binding.root)
        homeViewModel.attachView(this)

        binding.homeCompose.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                HomeScreenDashboard(
                    homeViewModel,
                    onRequest = {
                        startActivity(Intent(this@HomeFragment.requireContext(), ShowQrActivity::class.java).apply {
                            putExtra(Constants.QR_DATA, homeViewModel.homeUIState.value.vpa)
                        })
                    },
                    onPay = {
                        startActivity(Intent(this@HomeFragment.requireContext(), SendActivity::class.java))
                    })
            }
        }

        setUpSwipeRefresh()
        setupUi()
        showSwipeProgress()
        mHomePresenter?.fetchAccountDetails()
        mHomePresenter?.fetchVpa()
        mTvAccountBalance?.setOnClickListener {
            if (mTvAccountBalance?.text.toString() == Constants.TAP_TO_REVEAL) {
                homeScreenContainer?.let { it1 ->
                    TransitionManager.beginDelayedTransition(
                        it1
                    )
                }
                mTvAccountBalance?.text = accountBalance
                tvHideBalance?.visibility = View.VISIBLE
            }
        }
        tvHideBalance?.setOnClickListener {
            homeScreenContainer?.let { it1 -> TransitionManager.beginDelayedTransition(it1) }
            mTvAccountBalance?.text = Constants.TAP_TO_REVEAL
            tvHideBalance?.visibility = View.INVISIBLE
        }
        btnShowMoreTransactionsHistory?.setOnClickListener {
            homeScreenContainer?.let { it1 -> TransitionManager.beginDelayedTransition(it1) }
            mHistoryAdapter?.let { it1 -> mHomePresenter?.showMoreHistory(it1.itemCount) }
        }
        return binding.root
    }

    private fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            vStateView?.visibility = View.GONE
            rvHomeBottomSheetContent?.visibility = View.GONE
            btnShowMoreTransactionsHistory?.visibility = View.GONE
            tvLoadingTransactions?.visibility = View.VISIBLE
            progressBar?.visibility = View.VISIBLE
            mHomePresenter?.fetchAccountDetails()
        }
    }

    private fun setupUi() {
        hideBackButton()
        setupBottomSheet()
        setupRecyclerView()
        hideBottomSheetActionButton()
        rvHomeBottomSheetContent?.visibility = View.GONE
        vStateView?.visibility = View.GONE
    }

    private fun setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(vHomeBottomSheetDialog!!)
        mBottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> setSwipeEnabled(true)
                    else -> setSwipeEnabled(false)
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })
    }

    private fun setupRecyclerView() {
        rvHomeBottomSheetContent?.layoutManager =
            LinearLayoutManager(context)
        mHistoryAdapter?.setContext(activity)
        rvHomeBottomSheetContent?.adapter = mHistoryAdapter
    }

    override fun setPresenter(presenter: BaseHomeContract.HomePresenter?) {
        mHomePresenter = presenter
    }

    override fun setAccountBalance(account: Account?) {
        this.account = account
        val currencyCode = account?.currency?.code
        accountBalance = getFormattedAccountBalance(account?.balance, currencyCode)
        hideSwipeProgress()
        homeScreenContainer?.let { TransitionManager.beginDelayedTransition(it) }
        mTvAccountBalance?.text = Constants.TAP_TO_REVEAL
        tvHideBalance?.visibility = View.INVISIBLE
    }

    override fun showTransactionsHistory(transactions: List<Transaction?>?) {
        vStateView?.visibility = View.GONE
        hideTransactionLoading()
        btnShowMoreTransactionsHistory?.visibility = View.VISIBLE
        rvHomeBottomSheetContent?.visibility = View.VISIBLE
        mHistoryAdapter?.setData(transactions)
    }

    override fun showTransactionsError() {
        rvHomeBottomSheetContent?.visibility = View.GONE
        setupErrorStateView()
        vStateView?.visibility = View.VISIBLE
    }

    override fun showTransactionsEmpty() {
        rvHomeBottomSheetContent?.visibility = View.GONE
        setupEmptyStateView()
        vStateView?.visibility = View.VISIBLE
    }

    override fun showBottomSheetActionButton() {
        btnShowMoreTransactionsHistory?.visibility = View.VISIBLE
    }

    override fun hideBottomSheetActionButton() {
        btnShowMoreTransactionsHistory?.visibility = View.GONE
    }

    override fun hideSwipeProgress() {
        super.hideSwipeProgress()
    }

    override fun hideTransactionLoading() {
        tvLoadingTransactions?.visibility = View.GONE
        progressBar?.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        Toaster.showToast(context, message)
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(view, message)
    }

    private fun setupEmptyStateView() {
        if (activity != null) {
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(R.drawable.ic_empty_state))
            tvTransactionsStateTitle?.text =
                res.getString(R.string.empty_no_transaction_history_title)
            tvTransactionsStateSubtitle?.text =
                res.getString(R.string.empty_no_transaction_history_subtitle)
        }
    }

    private fun setupErrorStateView() {
        if (activity != null) {
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(R.drawable.ic_error_state))
            tvTransactionsStateTitle?.text = res.getString(R.string.error_oops)
            tvTransactionsStateSubtitle?.text = res
                .getString(R.string.error_no_transaction_history_subtitle)
        }
    }

    companion object {
        @JvmField
        var mBottomSheetBehavior: BottomSheetBehavior<*>? = null

        @JvmStatic
        fun newInstance(clientId: Long): HomeFragment {
            val args = Bundle()
            args.putLong(Constants.CLIENT_ID, clientId)
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}