package org.mifos.mobilewallet.mifospay.bank.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.adapters.UpiPinPagerAdapter
import org.mifos.mobilewallet.mifospay.utils.Constants

/**
 * Created by ankur on 16/July/2018
 */
@AndroidEntryPoint
class SetupUpiPinDialog : BottomSheetDialogFragment() {
    @JvmField
    @BindView(R.id.vp_setup_upi_pin)
    var mVpSetupUpiPin: ViewPager? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val type: String? = null
    private lateinit var mSetupUpiPinFragments: Array<Fragment?>
    private var upiPinPagerAdapter: UpiPinPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = View.inflate(context, R.layout.dialog_setup_upi_pin2, null)
        mBottomSheetBehavior = BottomSheetBehavior.from(rootView.parent as View)
        ButterKnife.bind(this, rootView)
        mSetupUpiPinFragments = arrayOfNulls(4)
        mSetupUpiPinFragments[0] = DebitCardFragment()
        mSetupUpiPinFragments[1] = OtpFragment()
        mSetupUpiPinFragments[2] = UpiPinFragment()
        mSetupUpiPinFragments[3] = UpiPinFragment()
        upiPinPagerAdapter = UpiPinPagerAdapter(
            childFragmentManager,
            mSetupUpiPinFragments
        )
        return rootView
    }

    override fun onStart() {
        super.onStart()
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        fun newInstance(type: String?): SetupUpiPinDialog {
            val args = Bundle()
            args.putString(Constants.TYPE, type)
            val fragment = SetupUpiPinDialog()
            fragment.arguments = args
            return fragment
        }
    }
}