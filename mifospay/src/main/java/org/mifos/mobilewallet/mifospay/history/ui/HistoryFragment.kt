package org.mifos.mobilewallet.mifospay.history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.core.domain.model.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.history.HistoryContract.HistoryView
import org.mifos.mobilewallet.mifospay.history.HistoryContract.TransactionsHistoryPresenter
import org.mifos.mobilewallet.mifospay.history.presenter.HistoryPresenter
import org.mifos.mobilewallet.mifospay.history.ui.adapter.HistoryAdapter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : BaseFragment(), HistoryView {
    @JvmField
    @Inject
    var mHistoryAdapter: HistoryAdapter? = null

    @JvmField
    @Inject
    var mPresenter: HistoryPresenter? = null
    var mTransactionsHistoryPresenter: TransactionsHistoryPresenter? = null

    @JvmField
    @BindView(R.id.cc_history_container)
    var historyContainer: ViewGroup? = null

    @JvmField
    @BindView(R.id.inc_state_view)
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
    @BindView(R.id.rv_history)
    var rvHistory: RecyclerView? = null

    @JvmField
    @BindView(R.id.pb_history)
    var pbHistory: ProgressBar? = null

    @JvmField
    @BindView(R.id.ll_filter_options)
    var filterLayout: LinearLayout? = null

    @JvmField
    @BindView(R.id.btn_filter_credits)
    var btnFilterCredits: Chip? = null

    @JvmField
    @BindView(R.id.btn_filter_all)
    var btnFilterAll: Chip? = null

    @JvmField
    @BindView(R.id.btn_filter_debits)
    var btnFilterDebits: Chip? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        setupUi()
        mPresenter!!.fetchTransactions()
        return rootView
    }

    private fun setupUi() {
        setupSwipeRefreshLayout()
        setupRecyclerView()
        vStateView!!.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        if (activity != null) {
            mHistoryAdapter!!.setContext(activity)
        }
        rvHistory!!.layoutManager = LinearLayoutManager(context)
        rvHistory!!.adapter = mHistoryAdapter
        rvHistory!!.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(childView: View, position: Int) {
                        mPresenter!!.handleTransactionClick(position)
                    }

                    override fun onItemLongPress(childView: View, position: Int) {}
                })
        )
    }

    private fun setupSwipeRefreshLayout() {
        setSwipeEnabled(true)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            mPresenter!!.fetchTransactions()
            displayAll()
        }
    }

    override fun showStateView(drawable: Int, title: Int, subtitle: Int) {
        TransitionManager.beginDelayedTransition(historyContainer!!)
        rvHistory!!.visibility = View.GONE
        pbHistory!!.visibility = View.GONE
        filterLayout!!.visibility = View.GONE
        vStateView!!.visibility = View.VISIBLE
        if (activity != null) {
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(drawable))
            tvTransactionsStateTitle
                ?.setText(res.getString(title))
            tvTransactionsStateSubtitle
                ?.setText(res.getString(subtitle))
        }
    }

    override fun showTransactions(transactions: List<Transaction>?) {
        showRecyclerView()
        mHistoryAdapter!!.setData(transactions)
    }

    override fun showEmptyTransactionTypeStateView(
        drawable: Int,
        title: String?,
        subtitle: String?
    ) {
        TransitionManager.beginDelayedTransition(historyContainer!!)
        rvHistory!!.visibility = View.GONE
        pbHistory!!.visibility = View.GONE
        filterLayout!!.visibility = View.VISIBLE
        vStateView!!.visibility = View.VISIBLE
        if (activity != null) {
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(drawable))
            tvTransactionsStateTitle?.text = title
            tvTransactionsStateSubtitle?.text = subtitle
        }
    }

    override fun showTransactionDetailDialog(transactionIndex: Int, accountNumber: String?) {
        if (activity != null) {
            val transactionDetailDialog = TransactionDetailDialog()
            val transactions = mHistoryAdapter!!.getTransactions()
            val arg = Bundle()
            arg.putParcelableArrayList(Constants.TRANSACTIONS, transactions)
            arg.putParcelable(Constants.TRANSACTION, transactions!![transactionIndex])
            arg.putString(Constants.ACCOUNT_NUMBER, accountNumber)
            transactionDetailDialog.arguments = arg
            transactionDetailDialog.show(
                requireActivity().supportFragmentManager,
                Constants.TRANSACTION_DETAILS
            )
        }
    }

    override fun showRecyclerView() {
        TransitionManager.beginDelayedTransition(historyContainer!!)
        vStateView!!.visibility = View.GONE
        pbHistory!!.visibility = View.GONE
        rvHistory!!.visibility = View.VISIBLE
        filterLayout!!.visibility = View.VISIBLE
    }

    override fun showHistoryFetchingProgress() {
        TransitionManager.beginDelayedTransition(historyContainer!!)
        vStateView!!.visibility = View.GONE
        rvHistory!!.visibility = View.GONE
        filterLayout!!.visibility = View.GONE
        pbHistory!!.visibility = View.VISIBLE
    }

    override fun refreshTransactions(transactions: List<Transaction>?) {
        showRecyclerView()
        mHistoryAdapter!!.setData(transactions)
    }

    override fun setPresenter(presenter: TransactionsHistoryPresenter?) {
        mTransactionsHistoryPresenter = presenter
    }

    @OnClick(R.id.btn_filter_all)
    fun displayAll() {
        btnFilterAll!!.isFocusable = true
        btnFilterAll!!.setChipBackgroundColorResource(R.color.clickedblue)
        btnFilterCredits!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        btnFilterDebits!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        mTransactionsHistoryPresenter!!.filterTransactionType(null)
    }

    @OnClick(R.id.btn_filter_credits)
    fun displayCredits() {
        btnFilterCredits!!.isFocusable = true
        btnFilterCredits!!.setChipBackgroundColorResource(R.color.clickedblue)
        btnFilterAll!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        btnFilterDebits!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        mTransactionsHistoryPresenter!!.filterTransactionType(TransactionType.CREDIT)
    }

    @OnClick(R.id.btn_filter_debits)
    fun displayDebits() {
        btnFilterDebits!!.isFocusable = true
        btnFilterDebits!!.setChipBackgroundColorResource(R.color.clickedblue)
        btnFilterAll!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        btnFilterCredits!!.setChipBackgroundColorResource(R.color.changedBackgroundColour)
        mTransactionsHistoryPresenter!!.filterTransactionType(TransactionType.DEBIT)
    }
}