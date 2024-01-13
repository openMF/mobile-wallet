package org.mifos.mobilewallet.mifospay.kyc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.kyc.KYCContract
import org.mifos.mobilewallet.mifospay.kyc.KYCContract.KYCLevel3View
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCLevel3Presenter
import javax.inject.Inject

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
class KYCLevel3Fragment : BaseFragment(), KYCLevel3View {
    @JvmField
    @Inject
    var mPresenter: KYCLevel3Presenter? = null
    var mKYCLevel3Presenter: KYCContract.KYCLevel3Presenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_kyc_lvl3, container, false)
        ButterKnife.bind(this, rootView)
        mPresenter!!.attachView(this)
        //setToolbarTitle(Constants.KYC_REGISTRATION_LEVEL_3);
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(): KYCLevel3Fragment {
            val args = Bundle()
            val fragment = KYCLevel3Fragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun setPresenter(presenter: KYCContract.KYCLevel3Presenter?) {
        mKYCLevel3Presenter = presenter
    }
}