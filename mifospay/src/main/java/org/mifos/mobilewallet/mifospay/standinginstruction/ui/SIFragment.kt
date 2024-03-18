package org.mifos.mobilewallet.mifospay.standinginstruction.ui

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
import org.mifos.mobilewallet.mifospay.databinding.FragmentSiBinding
import org.mifos.mobilewallet.mifospay.theme.MifosTheme
import org.mifos.mobilewallet.mifospay.utils.Constants

@AndroidEntryPoint
class SIFragment : BaseFragment() {
    private val newSIActivityRequestCode = 100
    lateinit var binding: FragmentSiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSiBinding.inflate(inflater, container, false)

        binding.siCompose.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MifosTheme {
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        SIScreen(
                            onItemClicked = {
                                onItemClick(it)
                            },
                            onNewSIClick = {onNewSIClick()}
                        )
                    }
                }
            }
        }
        return binding.root
    }

    private fun onNewSIClick() {
            val i = Intent(activity, NewSIActivity::class.java)
            startActivityForResult(i, newSIActivityRequestCode)
    }

    private fun onItemClick(standingInstructionId:Long){
        val intent = Intent(activity,SIDetailsActivity::class.java)
        standingInstructionId.let {
            intent.putExtra(Constants.SI_ID,standingInstructionId)
            startActivity(intent)
        }
    }
}