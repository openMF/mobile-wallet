package org.mifos.mobilewallet.mifospay.home.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.editprofile.ui.EditProfileActivity
import org.mifos.mobilewallet.mifospay.home.presenter.ProfileViewModel
import org.mifos.mobilewallet.mifospay.settings.ui.SettingsActivity
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.common.Constants

/**
 * Created by naman on 7/9/17.
 */
@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupUi()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    ProfileRoute(
                        onEditProfile = { onEditProfileClicked() },
                        onSettings = { onSettingsClicked() }
                    )
                }
            }
        }
    }

    private fun setupUi() {
        setToolbarTitle(Constants.PROFILE)
        setSwipeEnabled(false)
        hideBackButton()
    }

    private fun onEditProfileClicked() {
        if (activity != null) {
            activity?.startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }

    private fun onSettingsClicked() {
        if (activity != null) {
            activity?.startActivity(Intent(activity, SettingsActivity::class.java))
        }
    }

    companion object {
        @JvmField
        var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
        fun newInstance(clientId: Long): ProfileFragment {
            val args = Bundle()
            args.putLong(Constants.CLIENT_ID, clientId)
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}