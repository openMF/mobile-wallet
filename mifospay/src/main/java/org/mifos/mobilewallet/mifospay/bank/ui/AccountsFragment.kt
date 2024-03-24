package org.mifos.mobilewallet.mifospay.bank.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.hilt.android.AndroidEntryPoint
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.composeScreen.AccountScreen
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.adapters.BankAccountsAdapter
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountsPresenter
import org.mifos.mobilewallet.mifospay.bank.viewmodel.BankAccountsViewModel
import org.mifos.mobilewallet.mifospay.base.BaseFragment
import org.mifos.mobilewallet.mifospay.databinding.FragmentAccountsBinding
import org.mifos.mobilewallet.mifospay.utils.Constants
import org.mifos.mobilewallet.mifospay.utils.DebugUtil
import javax.inject.Inject

@AndroidEntryPoint
class AccountsFragment : BaseFragment() {

    lateinit var binding: FragmentAccountsBinding
    //hilt viewmodel
    private val viewModel: BankAccountsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAccountsBinding.inflate(inflater, container, false)
        ButterKnife.bind(this,binding.root)
        binding.accountScreen.setContent {
            AccountScreen( onAddAccountClicked = ::addAccountClicked ,
                onAccountClicked = ::onAccountClicked)
        }
        return binding.root
    }

    private fun onAccountClicked(bankAccountDetails: BankAccountDetails) {
        val intent = Intent(activity, BankAccountDetailActivity::class.java)
        intent.putExtra(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails)
        intent.putExtra(Constants.INDEX, viewModel.bankAccountDetailsList.indexOf(bankAccountDetails))
        startActivityForResult(intent, BANK_ACCOUNT_DETAILS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DebugUtil.log("rescode ", resultCode)
        if (requestCode == LINK_BANK_ACCOUNT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bundle = data!!.extras
            if (bundle != null) {
                DebugUtil.log("bundle", bundle)
            }
            if (bundle != null) {
                val bankAccountDetails = bundle.getParcelable<BankAccountDetails>(
                    Constants.NEW_BANK_ACCOUNT
                )
                if (bankAccountDetails != null) {
                    DebugUtil.log("details", bankAccountDetails)
                }
                if (bankAccountDetails != null) {
                    viewModel.bankAccountDetailsList.add(bankAccountDetails)
                }
            }
        } else if (requestCode == BANK_ACCOUNT_DETAILS_REQUEST_CODE && resultCode
            == Activity.RESULT_OK
        ) {
            val bundle = data!!.extras
            if (bundle != null) {
                DebugUtil.log("bundle", bundle)
            }
            if (bundle != null) {
                val bankAccountDetails = bundle.getParcelable<BankAccountDetails>(
                    Constants.UPDATED_BANK_ACCOUNT
                )
                val index = bundle.getInt(Constants.INDEX)
                if (bankAccountDetails != null) {
                    DebugUtil.log("details", bankAccountDetails)
                    viewModel.bankAccountDetailsList[index] = bankAccountDetails
                 }
            }
        }
    }

    fun addAccountClicked() {
        val intent = Intent(activity, LinkBankAccountActivity::class.java)
        startActivityForResult(intent, LINK_BANK_ACCOUNT_REQUEST_CODE)
    }

    companion object {
        const val LINK_BANK_ACCOUNT_REQUEST_CODE = 1
        const val BANK_ACCOUNT_DETAILS_REQUEST_CODE = 3
    }
}