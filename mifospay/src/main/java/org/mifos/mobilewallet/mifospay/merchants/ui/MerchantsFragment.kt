package org.mifos.mobilewallet.mifospay.merchants.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract
import org.mifos.mobilewallet.mifospay.merchants.adapter.MerchantsAdapter
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantsPresenter
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MerchantsFragment : BaseFragment(), MerchantsContract.MerchantsView {
    @JvmField
    @Inject
    var mPresenter: MerchantsPresenter? = null
    private lateinit var mMerchantsPresenter: MerchantsPresenter

    @JvmField
    @Inject
    var mMerchantsAdapter: MerchantsAdapter? = null

    @JvmField
    @BindView(R.id.inc_state_view)
    var vStateView: View? = null

    @JvmField
    @BindView(R.id.rv_merchants)
    var mRvMerchants: RecyclerView? = null

    @JvmField
    @BindView(R.id.iv_empty_no_transaction_history)
    var ivTransactionsStateIcon: ImageView? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_title)
    var tvTransactionsStateTitle: TextView? = null

    @JvmField
    @BindView(R.id.merchant_fragment_layout)
    var mMerchantFragmentLayout: View? = null

    @JvmField
    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    var tvTransactionsStateSubtitle: TextView? = null

    @JvmField
    @BindView(R.id.pb_merchants)
    var mMerchantProgressBar: ProgressBar? = null

    @JvmField
    @BindView(R.id.et_search_merchant)
    var etMerchantSearch: EditText? = null

    @JvmField
    @BindView(R.id.ll_search_merchant)
    var searchView: LinearLayout? = null
    private var merchantsList: List<SavingsWithAssociations>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_merchants, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter?.attachView(this)
        mMerchantsPresenter.fetchMerchants()
        setupUi()
        return rootView
    }

    private fun setupUi() {
        setUpSwipeRefreshLayout()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        mRvMerchants?.layoutManager =
            LinearLayoutManager(context)
        mRvMerchants?.adapter = mMerchantsAdapter
        mRvMerchants?.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(childView: View, position: Int) {
                        val merchantVPA: String? = mMerchantsAdapter?.merchants
                            ?.get(position)?.externalId
                        val intent = Intent(
                            activity,
                            MerchantTransferActivity::class.java
                        )
                        intent.putExtra(
                            Constants.MERCHANT_NAME, mMerchantsAdapter
                                ?.merchants?.get(position)?.clientName
                        )
                        intent.putExtra(
                            Constants.MERCHANT_VPA, mMerchantsAdapter
                                ?.merchants?.get(
                                    position
                                )?.externalId
                        )
                        intent.putExtra(
                            Constants.MERCHANT_ACCOUNT_NO, mMerchantsAdapter
                                ?.merchants?.get(position)?.accountNo
                        )
                        startActivity(intent)
                    }

                    override fun onItemLongPress(childView: View, position: Int) {
                        val clipboard = activity
                            ?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val merchantVPA: String? = mMerchantsAdapter?.merchants
                            ?.get(position)?.externalId
                        val clip: ClipData = ClipData.newPlainText("VPA", merchantVPA)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            activity,
                            R.string.vpa_copy_success,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        )
    }

    private fun setUpSwipeRefreshLayout() {
        setSwipeEnabled(true)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            mPresenter?.fetchMerchants()
        }
    }

    override fun showErrorStateView(drawable: Int, title: Int, subtitle: Int) {
        mRvMerchants?.visibility = View.GONE
        mMerchantProgressBar?.visibility = View.GONE
        hideSwipeProgress()
        vStateView?.visibility = View.VISIBLE
        if (activity != null) {
            val res: Resources = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(drawable))
            tvTransactionsStateTitle?.text = res.getString(title)
            tvTransactionsStateSubtitle?.text = res.getString(subtitle)
        }
    }

    override fun showEmptyStateView() {
        mMerchantFragmentLayout?.visibility = View.GONE
        mMerchantProgressBar?.visibility = View.GONE
        if (activity != null) {
            vStateView?.visibility = View.VISIBLE
            val res: Resources = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(R.drawable.ic_merchants))
            tvTransactionsStateTitle?.text = res.getString(R.string.empty_no_merchants_title)
            tvTransactionsStateSubtitle?.text = res.getString(R.string.empty_no_merchants_subtitle)
        }
    }

    override fun showMerchants() {
        mMerchantFragmentLayout?.visibility = View.VISIBLE
        vStateView?.visibility = View.GONE
        mMerchantProgressBar?.visibility = View.GONE
        searchView?.visibility = View.VISIBLE
    }

    @OnTextChanged(R.id.et_search_merchant)
    fun filerMerchants() {
        filterList(etMerchantSearch?.text.toString())
    }

    private fun filterList(text: String) {
        val merchantFilteredList: List<SavingsWithAssociations>?
        if (merchantsList != null) {
            merchantFilteredList = if (text.isBlank()) {
                merchantsList
            } else {
                val filteredList: MutableList<SavingsWithAssociations> =
                    ArrayList()
                for (merchant in merchantsList!!) {
                    if (merchant.externalId != null &&
                        merchant.externalId.toLowerCase(Locale.ROOT).contains(
                            text.toLowerCase(
                                Locale.ROOT
                            )
                        )
                    ) {
                        filteredList.add(merchant)
                    }
                    if (merchant.clientName.toLowerCase(Locale.ROOT).contains(
                            text.toLowerCase(Locale.ROOT)
                        )
                    ) {
                        filteredList.add(merchant)
                    }
                }
                filteredList
            }
            mMerchantsAdapter?.setData(merchantFilteredList)
        }
    }

    override fun setPresenter(presenter: MerchantsContract.MerchantsPresenter?) {
        mMerchantsPresenter = presenter as MerchantsPresenter
    }

    override fun listMerchantsData(savingsWithAssociationsList: List<SavingsWithAssociations>?) {
        merchantsList = savingsWithAssociationsList
        mMerchantsAdapter?.setData(savingsWithAssociationsList)
    }

    override fun showMerchantFetchProcess() {
        searchView?.visibility = View.GONE
        mMerchantFragmentLayout?.visibility = View.GONE
        vStateView?.visibility = View.GONE
        mMerchantProgressBar?.visibility = View.VISIBLE
    }
}