package org.mifos.mobilewallet.mifospay.bank.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.domain.model.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.BankAccountsView
import org.mifos.mobilewallet.mifospay.bank.adapters.BankAccountsAdapter
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountsPresenter
import org.mifos.mobilewallet.mifospay.bank.ui.BankAccountDetailActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class AccountsFragment : BaseFragment(), BankAccountsView {
    @JvmField
    @BindView(R.id.inc_state_view)
    var vStateView: View? = null

    @JvmField
    @Inject
    var mPresenter: BankAccountsPresenter? = null
    var mBankAccountsPresenter: BankContract.BankAccountsPresenter? = null

    @JvmField
    @BindView(R.id.rv_accounts)
    var mRvLinkedBankAccounts: RecyclerView? = null

    @JvmField
    @BindView(R.id.iv_empty_no_transaction_history)
    var ivTransactionsStateIcon: ImageView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_title)
    var tvTransactionsStateTitle: TextView? = null

    @JvmField
    @Inject
    var mBankAccountsAdapter: BankAccountsAdapter? = null

    @JvmField
    @BindView(R.id.linked_bank_account_text)
    var linkedAccountsText: TextView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    var tvTransactionsStateSubtitle: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_accounts, container, false)
        ButterKnife.bind(this, rootView)
        setupRecycletView()
        setUpSwipeRefresh()
        mPresenter!!.attachView(this)
        showSwipeProgress()
        mBankAccountsPresenter!!.fetchLinkedBankAccounts()
        return rootView
    }

    private fun setUpSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener { mBankAccountsPresenter!!.fetchLinkedBankAccounts() }
    }

    private fun setupRecycletView() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvLinkedBankAccounts!!.layoutManager = layoutManager
        mRvLinkedBankAccounts!!.setHasFixedSize(true)
        mRvLinkedBankAccounts!!.adapter = mBankAccountsAdapter
        mRvLinkedBankAccounts!!.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        mRvLinkedBankAccounts!!.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(childView: View, position: Int) {
                        val intent = Intent(
                            activity,
                            BankAccountDetailActivity::class.java
                        )
                        intent.putExtra(
                            Constants.BANK_ACCOUNT_DETAILS,
                            mBankAccountsAdapter!!.getBankDetails(position)
                        )
                        intent.putExtra(Constants.INDEX, position)
                        startActivityForResult(intent, BANK_ACCOUNT_DETAILS_REQUEST_CODE)
                    }

                    override fun onItemLongPress(childView: View, position: Int) {}
                })
        )
    }

    override fun showLinkedBankAccounts(bankAccountList: List<BankAccountDetails?>?) {
        if (bankAccountList == null || bankAccountList.size == 0) {
            mRvLinkedBankAccounts!!.visibility = View.GONE
            linkedAccountsText!!.visibility = View.GONE
            setupUi()
        } else {
            hideEmptyStateView()
            mRvLinkedBankAccounts!!.visibility = View.VISIBLE
            linkedAccountsText!!.visibility = View.VISIBLE
            mBankAccountsAdapter!!.setData(bankAccountList)
        }
        hideSwipeProgress()
    }

    override fun setPresenter(presenter: BankContract.BankAccountsPresenter?) {
        mBankAccountsPresenter = presenter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DebugUtil.log("rescode ", resultCode)
        if (requestCode == LINK_BANK_ACCOUNT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bundle = data!!.extras
            DebugUtil.log("bundle", bundle)
            if (bundle != null) {
                val bankAccountDetails = bundle.getParcelable<BankAccountDetails>(
                    Constants.NEW_BANK_ACCOUNT
                )
                DebugUtil.log("details", bankAccountDetails)
                mBankAccountsAdapter!!.addBank(bankAccountDetails)
                mRvLinkedBankAccounts!!.visibility = View.VISIBLE
                linkedAccountsText!!.visibility = View.GONE
            }
        } else if (requestCode == BANK_ACCOUNT_DETAILS_REQUEST_CODE && resultCode
            == Activity.RESULT_OK
        ) {
            val bundle = data!!.extras
            DebugUtil.log("bundle", bundle)
            if (bundle != null) {
                val bankAccountDetails = bundle.getParcelable<BankAccountDetails>(
                    Constants.UPDATED_BANK_ACCOUNT
                )
                val index = bundle.getInt(Constants.INDEX)
                mBankAccountsAdapter!!.setBankDetails(index, bankAccountDetails)
            }
        }
    }

    private fun setupUi() {
        showEmptyStateView()
    }

    private fun showEmptyStateView() {
        if (activity != null) {
            vStateView!!.visibility = View.VISIBLE
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(R.drawable.ic_accounts))
            tvTransactionsStateTitle?.text = res.getString(R.string.empty_no_accounts_title)
            tvTransactionsStateSubtitle?.text = res.getString(R.string.empty_no_accounts_subtitle)
        }
    }

    private fun hideEmptyStateView() {
        vStateView!!.visibility = View.GONE
    }

    @OnClick(R.id.addaccountbutton)
    fun addAccountClicked() {
        val intent = Intent(activity, LinkBankAccountActivity::class.java)
        startActivityForResult(intent, LINK_BANK_ACCOUNT_REQUEST_CODE)
    }

    companion object {
        const val LINK_BANK_ACCOUNT_REQUEST_CODE = 1
        const val BANK_ACCOUNT_DETAILS_REQUEST_CODE = 3
    }
}