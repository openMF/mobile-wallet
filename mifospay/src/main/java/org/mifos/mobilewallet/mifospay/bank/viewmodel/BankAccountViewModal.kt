package org.mifos.mobilewallet.mifospay.bank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.mifospay.bank.BankContract
import org.mifos.mobilewallet.mifospay.bank.presenter.BankAccountsPresenter
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class BankAccountsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val useCaseHandler: UseCaseHandler
) : ViewModel() {

    val bankAccountDetailsList: MutableList<BankAccountDetails> = mutableListOf()

    init {
         fetchLinkedBankAccounts()
    }

    fun fetchLinkedBankAccounts() {
        // Perform asynchronous operation to fetch bank accounts
        viewModelScope.launch {
            // Simulated data fetching process, replace it with actual data retrieval
            simulateDataFetch() 
        }
    }

    private suspend fun simulateDataFetch() {
        // Simulate data fetching process
        bankAccountDetailsList.add(
            BankAccountDetails(
                "SBI", "Ankur Sharma", "New Delhi",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "HDFC", "Mandeep Singh ", "Uttar Pradesh",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "ANDHRA", "Rakesh anna ", "Telegana",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "PNB", "luv Pro ", "Gujrat",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "HDF", "Harry potter ", "Hogwarts",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "GCI", "JIGME ", "JAMMU",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "FCI", "NISHU BOII ", "ASSAM",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "FCI", "NIRANJAN LAMICHHANE ", "ASSAM",
                Random().nextInt().toString() + " ", "Savings"
            )
        )
    }
}
