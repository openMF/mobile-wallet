package org.mifos.mobilewallet.mifospay.home.ui

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.history.ui.HistoryFragment
import org.mifos.mobilewallet.mifospay.home.adapter.TabLayoutAdapter
import org.mifos.mobilewallet.mifospay.invoice.ui.InvoicesFragment
import org.mifos.mobilewallet.mifospay.payments.ui.RequestFragment
import org.mifos.mobilewallet.mifospay.payments.ui.SendFragment
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.SIFragment
import org.mifos.mobilewallet.mifospay.utils.Utils.hideSoftKeyboard

class PaymentsFragment : BaseFragment() {
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
        val dim = 5f
        val bDim = 12f
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dim,
            r.displayMetrics
        )
        vpTabLayout!!.setPadding(px.toInt(), 0, px.toInt(), 0)
        vpTabLayout!!.clipToPadding = false
        vpTabLayout!!.pageMargin = bDim.toInt()
        setupUi()
        setupViewPager()
        tilTabLayout!!.setupWithViewPager(vpTabLayout)
        vpTabLayout!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (activity != null) {
                    hideSoftKeyboard(activity!!)
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
        return rootView
    }

    private fun setupUi() {
        setSwipeEnabled(false)
        setToolbarTitle(getString(R.string.payments))
    }

    private fun setupViewPager() {
        vpTabLayout!!.offscreenPageLimit = 1
        val tabLayoutAdapter = TabLayoutAdapter(childFragmentManager)
        tabLayoutAdapter.addFragment(SendFragment(), getString(R.string.send))
        tabLayoutAdapter.addFragment(RequestFragment(), getString(R.string.request))
        tabLayoutAdapter.addFragment(HistoryFragment(), getString(R.string.history))
        tabLayoutAdapter.addFragment(SIFragment(), getString(R.string.standing_instruction))
        tabLayoutAdapter.addFragment(InvoicesFragment(), getString(R.string.invoices))
        vpTabLayout!!.adapter = tabLayoutAdapter
    }

    companion object {
        fun newInstance(): PaymentsFragment {
            val args = Bundle()
            val fragment = PaymentsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}