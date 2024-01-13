package org.mifos.mobilewallet.mifospay.kyc.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.core.data.fineract.entity.kyc.KYCLevel1Details
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCDescriptionView
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCDescriptionPresenter
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
class KYCDescriptionFragment : BaseFragment(), KYCDescriptionView {
    @JvmField
    @Inject
    var mPresenter: KYCDescriptionPresenter? = null
    var mKYCDescriptionPresenter: KYCContract.KYCDescriptionPresenter? = null

    @JvmField
    @BindView(R.id.lv1)
    var lv1: LinearLayout? = null

    @JvmField
    @BindView(R.id.lv2)
    var lv2: LinearLayout? = null

    @JvmField
    @BindView(R.id.lv3)
    var lv3: LinearLayout? = null

    @JvmField
    @BindView(R.id.inc_state_view)
    var vStateView: View? = null

    @JvmField
    @BindView(R.id.pb_kyc)
    var pbKyc: ProgressBar? = null

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
    @BindView(R.id.levelcontainer)
    var frameLayout: FrameLayout? = null

    @JvmField
    @BindView(R.id.blv1)
    var btnLvl1: Button? = null

    @JvmField
    @BindView(R.id.blv2)
    var btnLvl2: Button? = null

    @JvmField
    @BindView(R.id.blv3)
    var btnLvl3: Button? = null

    @JvmField
    @BindView(R.id.completedtx1)
    var completedTxt1: TextView? = null

    @JvmField
    @BindView(R.id.completedtx2)
    var completedTxt2: TextView? = null

    @JvmField
    @BindView(R.id.completedtx3)
    var completedTxt3: TextView? = null

    @JvmField
    @BindView(R.id.tickimg1)
    var tickImg1: ImageView? = null

    @JvmField
    @BindView(R.id.tickimg2)
    var tickImg2: ImageView? = null

    @JvmField
    @BindView(R.id.tickimg3)
    var tickImg3: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_kyc, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        btnLvl1!!.isEnabled = false
        btnLvl2!!.isEnabled = false
        btnLvl3!!.isEnabled = false
        setUpSwipeRefreshLayout()
        mKYCDescriptionPresenter!!.fetchCurrentLevel()
        return rootView
    }

    @OnClick(R.id.blv1)
    fun onLevel1Clicked() {
        frameLayout!!.removeAllViews()
        replaceFragmentUsingFragmentManager(
            KYCLevel1Fragment.newInstance(), true,
            R.id.levelcontainer
        )
    }

    @OnClick(R.id.blv2)
    fun onLevel2Clicked() {
        frameLayout!!.removeAllViews()
        replaceFragmentUsingFragmentManager(
            KYCLevel1Fragment.newInstance(), true,
            R.id.levelcontainer
        )
        replaceFragmentUsingFragmentManager(
            KYCLevel2Fragment.newInstance(), true,
            R.id.levelcontainer
        )
    }

    @OnClick(R.id.blv3)
    fun onLevel3Clicked() {
        frameLayout!!.removeAllViews()
        replaceFragmentUsingFragmentManager(
            KYCLevel1Fragment.newInstance(), true,
            R.id.levelcontainer
        )
        replaceFragmentUsingFragmentManager(
            KYCLevel3Fragment.newInstance(), true,
            R.id.levelcontainer
        )
    }

    override fun showFetchingProcess() {
        frameLayout!!.visibility = View.GONE
        vStateView!!.visibility = View.GONE
        pbKyc!!.visibility = View.VISIBLE
    }

    private fun setUpSwipeRefreshLayout() {
        setSwipeEnabled(true)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            mPresenter!!.fetchCurrentLevel()
        }
    }

    override fun onFetchLevelSuccess(kycLevel1Details: KYCLevel1Details?) {
        hideSwipeProgress()
        pbKyc!!.visibility = View.GONE
        vStateView!!.visibility = View.GONE
        frameLayout!!.visibility = View.VISIBLE
        val currentLevel = kycLevel1Details!!.currentLevel.toInt()
        if (currentLevel >= 0) {
            btnLvl1!!.isEnabled = true
        }
        if (currentLevel >= 1) {
            btnLvl1!!.isEnabled = false
            btnLvl2!!.isEnabled = true
            tickImg1!!.setImageResource(R.drawable.ic_tick)
            completedTxt1!!.setText(R.string.completion)
            btnLvl1!!.setTextColor(resources.getColor(R.color.colorAccent))
            btnLvl1!!.background.setColorFilter(
                resources
                    .getColor(R.color.changedBackgroundColour), PorterDuff.Mode.MULTIPLY
            )
        }
        if (currentLevel >= 2) {
            btnLvl2!!.isEnabled = false
            btnLvl3!!.isEnabled = true
            tickImg2!!.setImageResource(R.drawable.ic_tick)
            completedTxt2!!.setText(R.string.completion)
            btnLvl2!!.setTextColor(resources.getColor(R.color.colorAccent))
            btnLvl2!!.background.setColorFilter(
                resources
                    .getColor(R.color.changedBackgroundColour), PorterDuff.Mode.MULTIPLY
            )
        }
        if (currentLevel >= 3) {
            btnLvl3!!.isEnabled = false
            tickImg3!!.setImageResource(R.drawable.ic_tick)
            completedTxt3!!.setText(R.string.completion)
            btnLvl3!!.setTextColor(resources.getColor(R.color.colorAccent))
            btnLvl3!!.background.setColorFilter(
                resources
                    .getColor(R.color.changedBackgroundColour), PorterDuff.Mode.MULTIPLY
            )
        }
    }

    override fun showErrorState(drawable: Int, errorTitle: Int, errorMessage: Int) {
        if (activity != null) {
            frameLayout!!.visibility = View.GONE
            pbKyc!!.visibility = View.GONE
            vStateView!!.visibility = View.VISIBLE
            val res = resources
            ivTransactionsStateIcon
                ?.setImageDrawable(res.getDrawable(drawable))
            tvTransactionsStateTitle
                ?.setText(errorTitle)
            tvTransactionsStateSubtitle
                ?.setText(errorMessage)
        }
    }

    override fun setPresenter(presenter: KYCContract.KYCDescriptionPresenter?) {
        mKYCDescriptionPresenter = presenter
    }

    companion object {
        fun newInstance(): KYCDescriptionFragment {
            val args = Bundle()
            val fragment = KYCDescriptionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}