package org.mifos.mobilewallet.mifospay.history.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.Transaction
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.history.HistoryContract
import org.mifos.mobilewallet.mifospay.history.HistoryContract.SpecificTransactionsView
import org.mifos.mobilewallet.mifospay.history.presenter.SpecificTransactionsPresenter
import org.mifos.mobilewallet.mifospay.history.ui.adapter.SpecificTransactionsAdapter
import org.mifos.mobilewallet.mifospay.receipt.ui.ReceiptActivity
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class SpecificTransactionsActivity : BaseActivity(), SpecificTransactionsView {
    @JvmField
    @Inject
    var mPresenter: SpecificTransactionsPresenter? = null
    var mSpecificTransactionsPresenter: HistoryContract.SpecificTransactionsPresenter? = null

    @JvmField
    @Inject
    var mSpecificTransactionsAdapter: SpecificTransactionsAdapter? = null

    @JvmField
    @BindView(R.id.rv_transactions)
    var mRvTransactions: RecyclerView? = null

    @JvmField
    @BindView(R.id.pb_specific_transaction)
    var progressBar: ProgressBar? = null

    @JvmField
    @BindView(R.id.iv_empty_no_transaction_history)
    var ivStateIcon: ImageView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_title)
    var tvStateTitle: TextView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    var tvStateSubtitle: TextView? = null

    @JvmField
    @BindView(R.id.error_state_view)
    var errorStateView: View? = null
    private var transactions: ArrayList<Transaction?>? = null
    private var secondAccountNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_transactions)
        ButterKnife.bind(this)
        mPresenter!!.attachView(this)
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        setToolbarTitle(Constants.SPECIFIC_TRANSACTIONS)
        transactions = intent.getParcelableArrayListExtra(Constants.TRANSACTIONS)
        secondAccountNumber = intent.getStringExtra(Constants.ACCOUNT_NUMBER)
        setupRecyclerView()
        mPresenter!!.getSpecificTransactions(transactions!!, secondAccountNumber!!)
    }

    private fun setupRecyclerView() {
        mRvTransactions!!.layoutManager = LinearLayoutManager(this)
        mRvTransactions!!.adapter = mSpecificTransactionsAdapter
        mRvTransactions!!.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        mRvTransactions!!.addOnItemTouchListener(
            RecyclerItemClickListener(this,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(childView: View?, position: Int) {
                        val intent = Intent(
                            this@SpecificTransactionsActivity,
                            ReceiptActivity::class.java
                        )
                        intent.data = Uri.parse(
                            Constants.RECEIPT_DOMAIN
                                    + mSpecificTransactionsAdapter!!.getTransaction(
                                position
                            ).transactionId
                        )
                        startActivity(intent)
                    }

                    override fun onItemLongPress(childView: View?, position: Int) {}
                })
        )
    }

    override fun showSpecificTransactions(specificTransactions: ArrayList<Transaction?>) {
        hideProgress()
        mSpecificTransactionsAdapter!!.setData(specificTransactions as ArrayList<Transaction>)
    }

    override fun showProgress() {
        mRvTransactions!!.visibility = View.GONE
        errorStateView!!.visibility = View.GONE
        progressBar!!.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar!!.visibility = View.GONE
        errorStateView!!.visibility = View.GONE
        mRvTransactions!!.visibility = View.VISIBLE
    }

    override fun showStateView(drawable: Int, title: Int, subtitle: Int) {
        mRvTransactions!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
        errorStateView!!.visibility = View.VISIBLE
        val res = resources
        ivStateIcon
            ?.setImageDrawable(res.getDrawable(drawable))
        tvStateTitle?.text = res.getString(title)
        tvStateSubtitle?.text = res.getString(subtitle)
    }

    override fun setPresenter(presenter: HistoryContract.SpecificTransactionsPresenter?) {
        mSpecificTransactionsPresenter = presenter
    }
}