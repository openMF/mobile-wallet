package org.mifospay.feature.bank.accounts.link

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifospay.core.model.domain.Bank
import com.mifospay.core.model.domain.BankAccountDetails
import com.mifospay.core.model.domain.BankType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.repository.local.MifosLocalAssetRepository
import org.mifospay.feature.bank.accounts.R
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class LinkBankAccountViewModel @Inject constructor(
    localAssetRepository: MifosLocalAssetRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private var selectedBank by mutableStateOf<Bank?>(null)

    private val _bankAccountDetails: MutableStateFlow<BankAccountDetails?> = MutableStateFlow(null)
    val bankAccountDetails: StateFlow<BankAccountDetails?> = _bankAccountDetails.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun updateSelectedBank(bank: Bank) {
        selectedBank = bank
    }

    val bankListUiState: StateFlow<BankUiState> = combine(
        _searchQuery,
        localAssetRepository.getBanks(),
        ::Pair
    ).map { searchQueryAndBanks ->
        val searchQuery = searchQueryAndBanks.first
        val localBanks = searchQueryAndBanks.second.map {
            Bank(it, R.drawable.ic_bank, BankType.OTHER)
        }
        val banks = ArrayList<Bank>().apply {
            addAll(popularBankList())
            addAll(localBanks)
        }.distinctBy { it.name }
        BankUiState.Success(
            banks.filter { it.name.contains(searchQuery.lowercase(), ignoreCase = true) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BankUiState.Loading,
    )

    private fun popularBankList(): List<Bank> {
        return listOf(
            Bank("RBL Bank", R.drawable.logo_rbl, BankType.POPULAR),
            Bank("SBI Bank", R.drawable.logo_sbi, BankType.POPULAR),
            Bank("PNB Bank", R.drawable.logo_pnb, BankType.POPULAR),
            Bank("HDFC Bank", R.drawable.logo_hdfc, BankType.POPULAR),
            Bank("ICICI Bank", R.drawable.logo_icici, BankType.POPULAR),
            Bank("AXIS Bank", R.drawable.logo_axis, BankType.POPULAR)
        )
    }

    fun fetchBankAccountDetails(onBankDetailsSuccess: () -> Unit) {
        // TODO:: UPI API implement, Implement with real API,
        //  It revert back to Account Screen after successful BankAccount Add
        _bankAccountDetails.update {
            BankAccountDetails(
                selectedBank?.name, "Ankur Sharma", "New Delhi",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        }
        onBankDetailsSuccess.invoke()
    }

    companion object {
        private val mRandom = Random()
    }
}

sealed interface BankUiState {
    data class Success(val banks: List<Bank> = emptyList()) : BankUiState
    data object Loading : BankUiState
}
