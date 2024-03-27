package org.mifos.mobilewallet.mifospay.bank.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.mobilewallet.core.repository.local.MifosLocalAssetRepository
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.ui.LinkBankAccountActivity
import org.mifos.mobilewallet.mifospay.domain.model.Bank
import java.util.Random
import javax.inject.Inject


@HiltViewModel
class LinkBankAccountViewModel @Inject constructor(
    val localAssetRepository: MifosLocalAssetRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _bankState = MutableStateFlow<BankUiState>(BankUiState.Loading)
    val bankState = _bankState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    init {
        getAllBanks()
    }

    private fun getAllBanks() {

        val banksListFlow = localAssetRepository.getBanks()

        viewModelScope.launch {
            val banksList =
                banksListFlow.map { banks ->
                    banks.map { bank ->
                        Bank(
                            bank,
                            R.drawable.ic_bank,
                            1
                        )
                    }
                }.stateIn(viewModelScope)

            _bankState.value =
                BankUiState.LoadedBankState(
                    popularBanks = getPopularBanks(),
                    allBanks = banksList.value
                )
        }
    }

    private fun getPopularBanks(): List<Bank> {
        return listOf(
            Bank("RBL Bank", R.drawable.logo_rbl, 0),
            Bank("SBI Bank", R.drawable.logo_sbi, 0),
            Bank("PNB Bank", R.drawable.logo_pnb, 0),
            Bank("HDFC Bank", R.drawable.logo_hdfc, 0),
            Bank("ICICI Bank", R.drawable.logo_icici, 0),
            Bank("AXIS Bank", R.drawable.logo_axis, 0)
        )
    }

    val bankListUiState: StateFlow<BankUiState> = combine(
        localAssetRepository.getBanks(),
        _searchQuery,
        ::Pair
    ).map { pair ->
        val banks = pair.first
        val allBanks = banks.map { Bank(it, R.drawable.ic_bank, 1) }
            .filter { it.name.contains(pair.second, ignoreCase = true) }

        BankUiState.LoadedBankState(popularBanks = getPopularBanks(), allBanks = allBanks)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BankUiState.LoadedBankState(
            popularBanks = emptyList(),
            allBanks = emptyList()
        ),
    )
    fun fetchBankAccountDetails(bankName: String?,activity:LinkBankAccountActivity) {
        // TODO:: UPI API implement
        activity.addBankAccount(
            BankAccountDetails(
                bankName, "Ankur Sharma", "New Delhi",
                mRandom.nextInt().toString() + " ", "Savings"
            )
        )
    }
    companion object{
        private val mRandom = Random()
    }
}

sealed interface BankUiState {
    data class LoadedBankState(
        val popularBanks: List<Bank>,
        val allBanks: List<Bank>
    ) : BankUiState
    data object Loading : BankUiState
}