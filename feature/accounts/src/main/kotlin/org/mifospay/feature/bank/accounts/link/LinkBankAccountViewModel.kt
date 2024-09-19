/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts.link

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifospay.core.model.domain.Bank
import com.mifospay.core.model.domain.BankAccountDetails
import com.mifospay.core.model.domain.BankType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.repository.local.LocalAssetRepository
import org.mifospay.feature.bank.accounts.R
import java.util.Random

class LinkBankAccountViewModel(
    localAssetRepository: LocalAssetRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private var selectedBank by mutableStateOf<Bank?>(null)

    private val accountDetails: MutableStateFlow<BankAccountDetails?> = MutableStateFlow(null)
    val bankAccountDetails: StateFlow<BankAccountDetails?> = accountDetails.asStateFlow()

    fun updateSearchQuery(query: String) {
        searchQuery.update { query }
    }

    fun updateSelectedBank(bank: Bank) {
        selectedBank = bank
    }

    val bankListUiState: StateFlow<BankUiState> = combine(
        searchQuery,
        localAssetRepository.getBanks(),
        ::Pair,
    ).map { searchQueryAndBanks ->
        val searchQuery = searchQueryAndBanks.first
        val localBanks = searchQueryAndBanks.second.map {
            Bank(it, R.drawable.feature_accounts_ic_bank, BankType.OTHER)
        }
        val banks = ArrayList<Bank>().apply {
            addAll(popularBankList())
            addAll(localBanks)
        }.distinctBy { it.name }
        BankUiState.Success(
            banks.filter { it.name.contains(searchQuery.lowercase(), ignoreCase = true) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BankUiState.Loading,
    )

    private fun popularBankList(): List<Bank> {
        return listOf(
            Bank("RBL Bank", R.drawable.feature_accounts_logo_rbl, BankType.POPULAR),
            Bank("SBI Bank", R.drawable.feature_accounts_logo_sbi, BankType.POPULAR),
            Bank("PNB Bank", R.drawable.feature_accounts_logo_pnb, BankType.POPULAR),
            Bank("HDFC Bank", R.drawable.feature_accounts_logo_hdfc, BankType.POPULAR),
            Bank("ICICI Bank", R.drawable.feature_accounts_logo_icici, BankType.POPULAR),
            Bank("AXIS Bank", R.drawable.feature_accounts_logo_axis, BankType.POPULAR),
        )
    }

    fun fetchBankAccountDetails(onBankDetailsSuccess: () -> Unit) {
        // TODO:: UPI API implement, Implement with real API,
        //  It revert back to Account Screen after successful BankAccount Add
        accountDetails.update {
            BankAccountDetails(
                selectedBank?.name,
                "Ankur Sharma",
                "New Delhi",
                mRandom.nextInt().toString() + " ",
                "Savings",
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
