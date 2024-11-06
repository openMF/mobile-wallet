/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifospay.core.model.domain.BankAccountDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random

class AccountViewModel : ViewModel() {

    private val _bankAccountDetailsList = MutableStateFlow<List<BankAccountDetails>>(emptyList())
    val bankAccountDetailsList: StateFlow<List<BankAccountDetails>> = _bankAccountDetailsList

    private val _accountsUiState = MutableStateFlow<AccountsUiState>(AccountsUiState.Loading)
    val accountsUiState: StateFlow<AccountsUiState> = _accountsUiState

    init {
        fetchLinkedAccount()
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            fetchLinkedAccount()
            _isRefreshing.emit(false)
        }
    }

    private val mRandom = Random()

    private fun fetchLinkedAccount() {
        viewModelScope.launch {
            _accountsUiState.value = AccountsUiState.Loading
            delay(2000)
            val linkedAccounts = fetchSampleLinkedAccounts()
            _bankAccountDetailsList.value = linkedAccounts
            _accountsUiState.value = if (linkedAccounts.isEmpty()) {
                AccountsUiState.Empty
            } else {
                AccountsUiState.LinkedAccounts(linkedAccounts)
            }
        }
    }

    private fun fetchSampleLinkedAccounts(): List<BankAccountDetails> {
        return listOf(
            BankAccountDetails(
                "SBI",
                "Ankur Sharma",
                "New Delhi",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
            BankAccountDetails(
                "HDFC",
                "Mandeep Singh",
                "Uttar Pradesh",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
            BankAccountDetails(
                "ANDHRA",
                "Rakesh anna",
                "Telegana",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
            BankAccountDetails(
                "PNB",
                "luv Pro",
                "Gujrat",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
            BankAccountDetails(
                "HDF",
                "Harry potter",
                "Hogwarts",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
            BankAccountDetails(
                "GCI",
                "JIGME",
                "JAMMU",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
            BankAccountDetails(
                "FCI",
                "NISHU BOII",
                "ASSAM",
                mRandom.nextInt().toString() + " ",
                "Savings",
            ),
        )
    }

    fun addBankAccount(bankAccountDetails: BankAccountDetails) {
        viewModelScope.launch {
            val updatedList = _bankAccountDetailsList.value.toMutableList().apply {
                add(bankAccountDetails)
            }
            _bankAccountDetailsList.value = updatedList
            _accountsUiState.value = AccountsUiState.LinkedAccounts(updatedList)
        }
    }

    fun updateBankAccount(index: Int, bankAccountDetails: BankAccountDetails) {
        viewModelScope.launch {
            val updatedList = _bankAccountDetailsList.value.toMutableList().apply {
                this[index] = bankAccountDetails
            }
            _bankAccountDetailsList.value = updatedList
            _accountsUiState.value = AccountsUiState.LinkedAccounts(updatedList)
        }
    }
}

sealed class AccountsUiState {
    data object Loading : AccountsUiState()
    data object Empty : AccountsUiState()
    data object Error : AccountsUiState()
    data class LinkedAccounts(val linkedAccounts: List<BankAccountDetails>) : AccountsUiState()
}
