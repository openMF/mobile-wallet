package org.mifos.mobilewallet.mifospay.invoice.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.Invoice
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract
import org.mifos.mobilewallet.mifospay.invoice.InvoiceContract.InvoicesView
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoicesPresenter
import org.mifos.mobilewallet.mifospay.invoice.ui.adapter.InvoicesAdapter
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import org.mifos.mobilewallet.mifospay.utils.Toaster
import javax.inject.Inject

@AndroidEntryPoint
class InvoicesFragment : BaseFragment(), InvoicesView {
    @JvmField
    @Inject
    var mPresenter: InvoicesPresenter? = null
    var mInvoicesPresenter: InvoiceContract.InvoicesPresenter? = null

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
    @BindView(R.id.tv_account_number)
    var mTvAccountNumber: TextView? = null

    @JvmField
    @BindView(R.id.tv_account_balance)
    var mTvAccountBalance: TextView? = null

    @JvmField
    @BindView(R.id.rv_invoices)
    var mRvInvoices: RecyclerView? = null

    @JvmField
    @BindView(R.id.pb_invoices)
    var pbInvoices: ProgressBar? = null

    @JvmField
    @Inject
    var mInvoicesAdapter: InvoicesAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_invoices, container, false)
        ButterKnife.bind(this, root)
        mPresenter!!.attachView(this)
        setupRecyclerView()
        setUpSwipeRefresh()
        showSwipeProgress()
        mInvoicesPresenter!!.fetchInvoices()
        return root
    }

    private fun setupRecyclerView() {
        mRvInvoices!!.layoutManager = LinearLayoutManager(activity)
        mRvInvoices!!.adapter = mInvoicesAdapter
        mRvInvoices!!.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        mRvInvoices!!.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(childView: View, position: Int) {
                        val intent = Intent(activity, InvoiceActivity::class.java)
                        // incomplete
                        intent.data = mInvoicesPresenter!!.getUniqueInvoiceLink(
                            mInvoicesAdapter!!.invoiceList!![position].id
                        )
                        startActivity(intent)
                    }

                    override fun onItemLongPress(childView: View, position: Int) {}
                })
        )
    }

    private fun setUpSwipeRefresh() {
        swipeRefreshLayout?.setOnRefreshListener {
            swipeRefreshLayout?.isRefreshing = false
            mInvoicesPresenter!!.fetchInvoices()
        }
    }

    override fun showInvoices(invoiceList: List<Invoice?>?) {
        if (invoiceList.isNullOrEmpty()) {
            DebugUtil.log("null")
            pbInvoices!!.visibility = View.GONE
            mRvInvoices!!.visibility = View.GONE
            showEmptyStateView()
        } else {
            DebugUtil.log("yes")
            pbInvoices?.visibility = View.GONE
            mRvInvoices?.visibility = View.VISIBLE
            mInvoicesAdapter?.setData(invoiceList as List<Invoice>)
            hideEmptyStateView()
        }
        mInvoicesAdapter?.setData(invoiceList as List<Invoice>)
        hideSwipeProgress()
    }

    private fun showEmptyStateView() {
        if (activity != null) {
            pbInvoices!!.visibility = View.GONE
            vStateView!!.visibility = View.VISIBLE
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(R.drawable.ic_invoices))
            tvTransactionsStateTitle?.text = res.getString(R.string.empty_no_invoices_title)
            tvTransactionsStateSubtitle?.text = res.getString(R.string.empty_no_invoices_subtitle)
        }
    }

    override fun showErrorStateView(drawable: Int, title: Int, subtitle: Int) {
        mRvInvoices!!.visibility = View.GONE
        pbInvoices!!.visibility = View.GONE
        hideSwipeProgress()
        vStateView!!.visibility = View.VISIBLE
        if (activity != null) {
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(drawable))
            tvTransactionsStateTitle?.text = res.getString(title)
            tvTransactionsStateSubtitle?.text = res.getString(subtitle)
        }
    }

    override fun showFetchingProcess() {
        vStateView!!.visibility = View.GONE
        mRvInvoices!!.visibility = View.GONE
        pbInvoices!!.visibility = View.VISIBLE
    }

    override fun setPresenter(presenter: InvoiceContract.InvoicesPresenter?) {
        mInvoicesPresenter = presenter
    }

    private fun hideEmptyStateView() {
        vStateView!!.visibility = View.GONE
    }

    override fun showSnackbar(message: String?) {
        Toaster.show(view, message)
    }
}