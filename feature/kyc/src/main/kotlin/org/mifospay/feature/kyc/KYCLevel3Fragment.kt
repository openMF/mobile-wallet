package org.mifospay.feature.kyc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import dagger.hilt.android.AndroidEntryPoint
import org.mifospay.base.BaseFragment
import org.mifospay.theme.MifosTheme

/**
 * Created by ankur on 17/May/2018
 */
@AndroidEntryPoint
class KYCLevel3Fragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    KYCLevel3Screen()
                }
            }
        }
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
}