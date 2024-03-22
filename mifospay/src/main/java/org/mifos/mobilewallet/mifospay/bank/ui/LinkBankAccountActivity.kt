package org.mifos.mobilewallet.mifospay.bank.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.BankContract.LinkBankAccountView
import org.mifos.mobilewallet.mifospay.bank.adapters.OtherBankAdapter
import org.mifos.mobilewallet.mifospay.bank.adapters.PopularBankAdapter
import org.mifos.mobilewallet.mifospay.bank.presenter.LinkBankAccountPresenter
import org.mifos.mobilewallet.mifospay.base.BaseActivity
import org.mifos.mobilewallet.mifospay.domain.model.Bank
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import org.mifos.mobilewallet.mifospay.utils.FileUtils
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener
import org.mifos.mobilewallet.mifospay.utils.RecyclerItemClickListener.SimpleOnItemClickListener
import org.mifos.mobilewallet.mifospay.utils.Utils.isBlank
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LinkBankAccountActivity : BaseActivity(), LinkBankAccountView {
    @JvmField
    @Inject
    var mPresenter: LinkBankAccountPresenter? = null
    var mLinkBankAccountPresenter: BankContract.LinkBankAccountPresenter? = null

    @JvmField
    @BindView(R.id.et_search_bank)
    var mEtSearchBank: EditText? = null

    @JvmField
    @BindView(R.id.rv_popular_banks)
    var mRvPopularBanks: RecyclerView? = null

    @JvmField
    @BindView(R.id.rv_other_banks)
    var mRvOtherBanks: RecyclerView? = null

    @JvmField
    @BindView(R.id.popular_banks)
    var mPopularBanks: TextView? = null

    @JvmField
    @BindView(R.id.other_banks)
    var mOtherBanks: TextView? = null

    @JvmField
    @BindView(R.id.no_bank_found)
    var mNoBankFound: TextView? = null

    @JvmField
    @Inject
    var mPopularBankAdapter: PopularBankAdapter? = null

    @JvmField
    @Inject
    var mOtherBankAdapter: OtherBankAdapter? = null
    private var banksList: ArrayList<Bank>? = null
    private var popularBanks: ArrayList<Bank>? = null
    private var bankSelected: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_bank_account)
        ButterKnife.bind(this)
        setToolbarTitle("Link Bank Account")
        showColoredBackButton(Constants.BLACK_BACK_BUTTON)
        mPresenter!!.attachView(this)
        showProgressDialog(Constants.PLEASE_WAIT)
        setupRecyclerview()
        mRvOtherBanks!!.isNestedScrollingEnabled = false
        setupAdapterData()
        hideProgressDialog()
    }

    private fun setupRecyclerview() {
        val gridManager: LinearLayoutManager = GridLayoutManager(this, 3)
        gridManager.orientation = GridLayoutManager.VERTICAL
        mRvPopularBanks!!.layoutManager = gridManager
        mRvPopularBanks!!.setHasFixedSize(true)
        mPopularBankAdapter!!.setContext(this)
        mRvPopularBanks!!.adapter = mPopularBankAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvOtherBanks!!.layoutManager = layoutManager
        mRvOtherBanks!!.setHasFixedSize(true)
        mOtherBankAdapter!!.setContext(this)
        mRvOtherBanks!!.adapter = mOtherBankAdapter
        mRvOtherBanks!!.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        mRvPopularBanks!!.addOnItemTouchListener(
            RecyclerItemClickListener(this,
                object : SimpleOnItemClickListener() {
                    override fun onItemClick(childView: View?, position: Int) {
                        val bank = mPopularBankAdapter!!.getBank(position)
                        bankSelected = bank.name
                        val chooseSimDialog = ChooseSimDialog()
                        chooseSimDialog.show(supportFragmentManager, "Choose Sim Dialog")
                    }
                })
        )
        mRvOtherBanks!!.addOnItemTouchListener(
            RecyclerItemClickListener(this,
                object : SimpleOnItemClickListener() {
                    override fun onItemClick(childView: View?, position: Int) {
                        val bank = mOtherBankAdapter!!.getBank(position)
                        bankSelected = bank.name
                        val chooseSimDialog = ChooseSimDialog()
                        chooseSimDialog.show(supportFragmentManager, "Choose Sim Dialog")
                    }
                })
        )
        mEtSearchBank!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                Log.d("qxz", "onTextChanged: " + s.toString());
//                mOtherBankAdapter.getFilter().filter(mEtSearchBank.getText().toString());
                filter(mEtSearchBank!!.text.toString().trim { it <= ' ' })
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun filter(text: String) {
        var filteredList: MutableList<Bank>? = ArrayList()
        if (text.isBlank()) {
            mRvPopularBanks!!.visibility = View.VISIBLE
            mPopularBanks!!.visibility = View.VISIBLE
            filteredList = banksList
        } else {
            mRvPopularBanks!!.visibility = View.GONE
            mPopularBanks!!.visibility = View.GONE
            for (bank in banksList!!) {
                if (bank.name.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {
                    filteredList!!.add(bank)
                }
            }
        }
        mOtherBankAdapter!!.filterList(filteredList)
        if (filteredList!!.isEmpty()) {
            mNoBankFound!!.visibility = View.VISIBLE
            mOtherBanks!!.visibility = View.GONE
        } else {
            mNoBankFound!!.visibility = View.GONE
            mOtherBanks!!.visibility = View.GONE
        }
    }

    private fun setupAdapterData() {
        val jsonObject: JSONObject
        try {
            jsonObject = FileUtils.readJson(this, "banks.json")!!
            banksList = ArrayList()
            for (i in 0 until jsonObject.getJSONArray("banks").length()) {
                banksList!!.add(
                    Bank(
                        (jsonObject.getJSONArray("banks")[i] as String),
                        R.drawable.ic_bank, 1
                    )
                )
            }
            popularBanks = ArrayList()
            popularBanks!!.add(Bank("RBL Bank", R.drawable.logo_rbl, 0))
            popularBanks!!.add(Bank("SBI Bank", R.drawable.logo_sbi, 0))
            popularBanks!!.add(Bank("PNB Bank", R.drawable.logo_pnb, 0))
            popularBanks!!.add(Bank("HDFC Bank", R.drawable.logo_hdfc, 0))
            popularBanks!!.add(Bank("ICICI Bank", R.drawable.logo_icici, 0))
            popularBanks!!.add(Bank("AXIS Bank", R.drawable.logo_axis, 0))
            DebugUtil.log(popularBanks!!, banksList!!)
            mPopularBankAdapter!!.setData(popularBanks)
            mOtherBankAdapter!!.setData(banksList)
        } catch (e: Exception) {
            e.message?.let { DebugUtil.log(it) }
        }
    }

    fun linkBankAccount(selectedSim: Int) {
        showProgressDialog(Constants.VERIFYING_MOBILE_NUMBER)
        mLinkBankAccountPresenter!!.fetchBankAccountDetails(bankSelected)
    }

    override fun addBankAccount(bankAccountDetails: BankAccountDetails?) {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent()
            intent.putExtra(Constants.NEW_BANK_ACCOUNT, bankAccountDetails)
            setResult(RESULT_OK, intent)
            hideProgressDialog()
            finish()
        }, 1500)
    }

    override fun setPresenter(presenter: BankContract.LinkBankAccountPresenter?) {
        mLinkBankAccountPresenter = presenter
    }

    override fun onBackPressed() {
        if (mEtSearchBank!!.text.length != 0) {
            mEtSearchBank!!.text.clear()
        } else {
            super.onBackPressed()
        }
    }
}