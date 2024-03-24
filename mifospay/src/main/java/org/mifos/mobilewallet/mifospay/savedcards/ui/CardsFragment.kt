package org.mifos.mobilewallet.mifospay.savedcards.ui


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import dagger.hilt.android.AndroidEntryPoint
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.databinding.FragmentCardsBinding
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme

/**
 * This is the UI component of the SavedCards Architecture.
 * @author ankur
 * @since 21/May/2018
 */
@AndroidEntryPoint
class CardsFragment : BaseFragment()  {

    private lateinit var binding: FragmentCardsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsBinding.inflate(inflater, container, false)

        binding.composeViewCards.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        CardsScreen(
                            onEditCard = {
                                // TODO open Add card flow with edit flag to allow edit the card
                            },
                            onAddBtn = { onAddBtnClicked() },
                        )
                    }
                }
            }
        }
        return binding.root
    }

    private fun onAddBtnClicked() {
        if (activity != null) {
            activity?.startActivity(Intent(activity, AddCardDialog::class.java))
        }
    }

    companion object {
        fun newInstance(): CardsFragment {
            val args = Bundle()
            val fragment = CardsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}