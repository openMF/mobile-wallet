package org.mifos.mobilewallet.mifospay.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.savedcards.ui.AddCardDialog
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

class FinanceFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupUi()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    FinanceScreen(onAddBtn = { onAddBtnClicked() })
                }
            }
        }
    }

    private fun setupUi() {
        setSwipeEnabled(false)
        setToolbarTitle(getString(R.string.finance))
    }

    private fun onAddBtnClicked() {
        if (activity != null) {
            activity?.startActivity(Intent(activity, AddCardDialog::class.java))
        }
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