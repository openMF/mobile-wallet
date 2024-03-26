package org.mifos.mobilewallet.mifospay.bank.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor() : ViewModel() {

    val bankAccountDetailsList: MutableList<BankAccountDetails> = mutableListOf()

    private val _accountUiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val accountsUiState: StateFlow<AccountsUiState> = _accountUiState

    private val mRandom = Random()
    val isRefreshing = MutableStateFlow(false)

    init {
        fetchLinkedAccount()
    }

    /**
     * Mocks network delay
     */
    fun fetchLinkedAccount() {
       viewModelScope.launch {
           _accountUiState.value = AccountsUiState.Loading
           delay(2000)
           val linkedAccount = fetchSampleLinkedAccounts()
           _accountUiState.value = AccountsUiState.LinkedAccounts(linkedAccount)
       }
    }

    fun fetchSampleLinkedAccounts(): MutableList<BankAccountDetails> {
        bankAccountDetailsList.add(
            BankAccountDetails(
                "SBI", "Ankur Sharma", "New Delhi",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "HDFC", "Mandeep Singh ", "Uttar Pradesh",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "ANDHRA", "Rakesh anna ", "Telegana",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "PNB", "luv Pro ", "Gujrat",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "HDF", "Harry potter ", "Hogwarts",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "GCI", "JIGME ", "JAMMU",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        bankAccountDetailsList.add(
            BankAccountDetails(
                "FCI", "NISHU BOII ", "ASSAM",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
        return bankAccountDetailsList
    }
    fun refreshAccountList(){
        isRefreshing.value = true
        fetchLinkedAccount()
        isRefreshing.value = false
    }

}

sealed class AccountsUiState {
    data object Loading : AccountsUiState()
    data object Empty : AccountsUiState()
    data object Error : AccountsUiState()
    data class LinkedAccounts(val linkedAccounts: List<BankAccountDetails>) : AccountsUiState()
}
