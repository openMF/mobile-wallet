package org.mifos.mobilewallet.mifospay.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.payments.presenter.TransferViewModel
import org.mifos.mobilewallet.mifospay.qr.ui.ShowQrActivity
import org.mifos.mobilewallet.mifospay.standinginstruction.ui.NewSIActivity
import org.mifos.mobilewallet.mifospay.theme.MifosTheme

@AndroidEntryPoint
class PaymentsFragment : BaseFragment() {

    private val transferViewModel: TransferViewModel by viewModels()
    private val newSIActivityRequestCode = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupUi()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    PaymentsRoute(
                        showQr = { showQrClicked() },
                        onNewSI = { onNewSI() }
                    )
                }
            }
        }
    }

    private fun setupUi() {
        setSwipeEnabled(false)
        setToolbarTitle(getString(R.string.payments))
    }

    private fun showQrClicked() {
        val intent = Intent(activity, ShowQrActivity::class.java)
        intent.putExtra(Constants.QR_DATA, transferViewModel.vpa.value)
        startActivity(intent)
    }

    private fun onNewSI() {
        val i = Intent(activity, NewSIActivity::class.java)
        startActivityForResult(i, newSIActivityRequestCode)
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