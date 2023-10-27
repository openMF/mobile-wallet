package org.mifos.mobilewallet.mifospay.home.ui

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.ui.AccountsFragment
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.home.adapter.TabLayoutAdapter
import org.mifos.mobilewallet.mifospay.kyc.ui.KYCDescriptionFragment
import org.mifos.mobilewallet.mifospay.merchants.ui.MerchantsFragment
import org.mifos.mobilewallet.mifospay.savedcards.ui.CardsFragment

class FinanceFragment : BaseFragment() {
    @JvmField
    @BindView(R.id.vp_tab_layout)
    var vpTabLayout: ViewPager? = null

    @JvmField
    @BindView(R.id.tl_tab_layout)
    var tilTabLayout: TabLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_finance, container, false)
        ButterKnife.bind(this, rootView)
        setupUi()
        setupViewPager()
        tilTabLayout!!.setupWithViewPager(vpTabLayout)
        return rootView
    }

    private fun setupUi() {
        setSwipeEnabled(false)
        setToolbarTitle(getString(R.string.finance))
    }

    private fun setupViewPager() {
        vpTabLayout!!.offscreenPageLimit = 1
        val tabLayoutAdapter = TabLayoutAdapter(childFragmentManager)
        tabLayoutAdapter.addFragment(AccountsFragment(), getString(R.string.accounts))
        tabLayoutAdapter.addFragment(CardsFragment(), getString(R.string.cards))
        tabLayoutAdapter.addFragment(MerchantsFragment(), getString(R.string.merchants))
        tabLayoutAdapter.addFragment(KYCDescriptionFragment(), getString(R.string.kyc))
        vpTabLayout!!.adapter = tabLayoutAdapter
    }

    companion object {
        fun newInstance(): FinanceFragment {
            val args = Bundle()
            val fragment = FinanceFragment()
            fragment.arguments = args
            return fragment
        }
    }
}