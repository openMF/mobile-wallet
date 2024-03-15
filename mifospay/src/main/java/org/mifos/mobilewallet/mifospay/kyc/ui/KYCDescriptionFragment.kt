package org.mifos.mobilewallet.mifospay.kyc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.kyc.presenter.KYCDescriptionViewModel
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
class KYCDescriptionFragment : BaseFragment() {

    private val kviewModel: KYCDescriptionViewModel by viewModels()
    private var frameLayout: FrameLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return  ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    KYCDescriptionScreen(
                        kviewModel,
                        onLevel1Clicked = { onLevel1Clicked() },
                        onLevel2Clicked = { onLevel2Clicked() },
                        onLevel3Clicked = { onLevel3Clicked() }
                    )
                }
            }
        }
    }

    private fun onLevel1Clicked() {
        frameLayout!!.removeAllViews()
        replaceFragmentUsingFragmentManager(
            KYCLevel1Fragment.newInstance(), true,
            R.id.levelcontainer
        )
    }

    private fun onLevel2Clicked() {
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

    private fun onLevel3Clicked() {
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

    companion object {
        fun newInstance(): KYCDescriptionFragment {
            val args = Bundle()
            val fragment = KYCDescriptionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}