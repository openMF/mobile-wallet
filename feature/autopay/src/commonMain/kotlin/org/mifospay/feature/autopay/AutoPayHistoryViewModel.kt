/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.autopay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mifospay.core.common.ScreenState
import org.mifospay.core.data.repository.AutoPayHistoryRepository
import org.mifospay.core.data.repository.AutoPayHistoryStatistics
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.model.autopay.PaymentStatus
import org.mifospay.core.network.model.entity.Page

/**
 * ViewModel for AutoPay history screen.
 *
 * This ViewModel provides read-only access to AutoPay history data.
 * Users can view, search, and filter history but cannot edit or delete entries.
 */
class AutoPayHistoryViewModel(
    private val autoPayHistoryRepository: AutoPayHistoryRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val autoPayId: Long = savedStateHandle.get<Long>("autoPayId") ?: 0L

    private val _uiState = MutableStateFlow(AutoPayHistoryUiState())
    val uiState: StateFlow<AutoPayHistoryUiState> = _uiState.asStateFlow()

    init {
        loadAutoPayHistory()
        loadHistoryStatistics()
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _selectedDateRange = MutableStateFlow<Pair<String?, String?>>(null to null)
    val selectedDateRange: StateFlow<Pair<String?, String?>> = _selectedDateRange.asStateFlow()

    private fun loadAutoPayHistory() {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Loading history for AutoPay ID: $autoPayId")

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                delay(2000)

                val dummyHistory = createDummyHistoryData()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    historyList = dummyHistory,
                    error = null,
                )
                Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Successfully loaded ${dummyHistory.size} dummy history entries")
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "Unknown error occurred",
                )
                Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Exception in history flow: ${exception.message}")
            }
        }
    }

    fun loadAutoPayHistoryWithPagination(
        autoPayId: Long,
        limit: Int = 20,
        offset: Int = 0,
    ) {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Loading paginated history for AutoPay ID: $autoPayId, limit: $limit, offset: $offset")

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            autoPayHistoryRepository.getAutoPayHistoryWithPagination(autoPayId, limit, offset)
                .onEach { screenState ->
                    when (screenState) {
                        is ScreenState.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        }

                        is ScreenState.Empty -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                historyPage = null,
                                error = null,
                            )
                        }

                        is ScreenState.Content -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                historyPage = screenState.data,
                                error = null,
                            )
                            Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Successfully loaded paginated history: ${screenState.data.pageItems.size} entries")
                        }

                        is ScreenState.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = screenState.error.message ?: "Failed to load history",
                            )
                            Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Error loading paginated history: ${screenState.error.message}")
                        }

                        is ScreenState.NoNetwork -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "No network. Please check your connection.",
                            )
                        }

                        is ScreenState.Unauthenticated -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Session expired. Please log in again.",
                            )
                        }
                    }
                }
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred",
                    )
                    Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Exception in paginated history flow: ${exception.message}")
                }
                .launchIn(this)
        }
    }

    private fun loadHistoryStatistics() {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Loading statistics for AutoPay ID: $autoPayId")

        viewModelScope.launch {
            try {
                delay(1500)

                val dummyStatistics = createDummyStatistics()
                _uiState.value = _uiState.value.copy(statistics = dummyStatistics)
                Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Successfully loaded dummy statistics: ${dummyStatistics.totalTransactions} total transactions")
            } catch (exception: Exception) {
                Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Exception in statistics flow: ${exception.message}")
            }
        }
    }

    fun searchHistory(query: String) {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Searching history with query: $query")

        _searchQuery.value = query

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(filteredHistoryList = _uiState.value.historyList)
            return
        }

        viewModelScope.launch {
            autoPayHistoryRepository.searchHistory(query)
                .onEach { screenState ->
                    when (screenState) {
                        is ScreenState.Content -> {
                            _uiState.value = _uiState.value.copy(filteredHistoryList = screenState.data)
                            Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Search completed: ${screenState.data.size} results")
                        }

                        is ScreenState.Empty -> {
                            _uiState.value = _uiState.value.copy(filteredHistoryList = emptyList())
                        }

                        is ScreenState.Error -> {
                            Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Error searching history: ${screenState.error.message}")
                        }

                        is ScreenState.Loading,
                        is ScreenState.NoNetwork,
                        is ScreenState.Unauthenticated,
                        -> {
                            // Search loading + non-fatal transient states handled separately
                        }
                    }
                }
                .catch { exception ->
                    Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Exception in search flow: ${exception.message}")
                }
                .launchIn(this)
        }
    }

    fun filterByStatus(status: String?) {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Filtering by status: $status")

        _selectedStatus.value = status

        if (status == null) {
            _uiState.value = _uiState.value.copy(filteredHistoryList = _uiState.value.historyList)
            return
        }

        viewModelScope.launch {
            autoPayHistoryRepository.getHistoryByStatus(status)
                .onEach { screenState ->
                    when (screenState) {
                        is ScreenState.Content -> {
                            _uiState.value = _uiState.value.copy(filteredHistoryList = screenState.data)
                            Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Status filter applied: ${screenState.data.size} results")
                        }

                        is ScreenState.Empty -> {
                            _uiState.value = _uiState.value.copy(filteredHistoryList = emptyList())
                        }

                        is ScreenState.Error -> {
                            Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Error filtering by status: ${screenState.error.message}")
                        }

                        is ScreenState.Loading,
                        is ScreenState.NoNetwork,
                        is ScreenState.Unauthenticated,
                        -> {
                            // Filter loading + non-fatal transient states handled separately
                        }
                    }
                }
                .catch { exception ->
                    Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Exception in status filter flow: ${exception.message}")
                }
                .launchIn(this)
        }
    }

    fun filterByDateRange(fromDate: String?, toDate: String?) {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Filtering by date range: $fromDate to $toDate")

        _selectedDateRange.value = fromDate to toDate

        if (fromDate == null || toDate == null) {
            _uiState.value = _uiState.value.copy(filteredHistoryList = _uiState.value.historyList)
            return
        }

        viewModelScope.launch {
            autoPayHistoryRepository.getHistoryByDateRange(fromDate, toDate)
                .onEach { screenState ->
                    when (screenState) {
                        is ScreenState.Content -> {
                            _uiState.value = _uiState.value.copy(filteredHistoryList = screenState.data)
                            Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Date range filter applied: ${screenState.data.size} results")
                        }

                        is ScreenState.Empty -> {
                            _uiState.value = _uiState.value.copy(filteredHistoryList = emptyList())
                        }

                        is ScreenState.Error -> {
                            Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Error filtering by date range: ${screenState.error.message}")
                        }

                        is ScreenState.Loading,
                        is ScreenState.NoNetwork,
                        is ScreenState.Unauthenticated,
                        -> {
                            // Filter loading + non-fatal transient states handled separately
                        }
                    }
                }
                .catch { exception ->
                    Logger.e("AUTOPAY_HISTORY AutoPayHistoryViewModel Exception in date range filter flow: ${exception.message}")
                }
                .launchIn(this)
        }
    }

    fun clearFilters() {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Clearing all filters")

        _searchQuery.value = ""
        _selectedStatus.value = null
        _selectedDateRange.value = null to null
        _uiState.value = _uiState.value.copy(filteredHistoryList = _uiState.value.historyList)
    }

    fun refreshHistory() {
        Logger.d("AUTOPAY_HISTORY AutoPayHistoryViewModel Refreshing history for AutoPay ID: $autoPayId")

        loadAutoPayHistory()
        loadHistoryStatistics()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun createDummyHistoryData(): List<AutoPayHistory> {
        return listOf(
            AutoPayHistory(
                id = 1L,
                autoPayId = autoPayId,
                amount = 1200.00,
                currency = "USD",
                status = PaymentStatus.COMPLETED,
                transactionDate = "Jan 15, 2025",
                recipientName = "Landlord Corp",
                recipientAccountNumber = "****1234",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001234",
                failureReason = null,
                createdDate = "Jan 15, 2025 10:30 AM",
            ),
            AutoPayHistory(
                id = 2L,
                autoPayId = autoPayId,
                amount = 89.99,
                currency = "USD",
                status = PaymentStatus.COMPLETED,
                transactionDate = "Jan 10, 2025",
                recipientName = "Comcast",
                recipientAccountNumber = "****5678",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001235",
                failureReason = null,
                createdDate = "Jan 10, 2025 08:15 AM",
            ),
            AutoPayHistory(
                id = 3L,
                autoPayId = autoPayId,
                amount = 156.75,
                currency = "USD",
                status = PaymentStatus.FAILED,
                transactionDate = "Jan 5, 2025",
                recipientName = "Power Company",
                recipientAccountNumber = "****9012",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001236",
                failureReason = "Insufficient funds",
                createdDate = "Jan 5, 2025 14:20 PM",
            ),
            AutoPayHistory(
                id = 4L,
                autoPayId = autoPayId,
                amount = 85.50,
                currency = "USD",
                status = PaymentStatus.COMPLETED,
                transactionDate = "Dec 28, 2024",
                recipientName = "Verizon",
                recipientAccountNumber = "****3456",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001237",
                failureReason = null,
                createdDate = "Dec 28, 2024 09:45 AM",
            ),
            AutoPayHistory(
                id = 5L,
                autoPayId = autoPayId,
                amount = 45.00,
                currency = "USD",
                status = PaymentStatus.COMPLETED,
                transactionDate = "Dec 20, 2024",
                recipientName = "Fitness Center",
                recipientAccountNumber = "****7890",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001238",
                failureReason = null,
                createdDate = "Dec 20, 2024 07:30 AM",
            ),
            AutoPayHistory(
                id = 6L,
                autoPayId = autoPayId,
                amount = 15.99,
                currency = "USD",
                status = PaymentStatus.UPCOMING,
                transactionDate = "Dec 15, 2024",
                recipientName = "Netflix",
                recipientAccountNumber = "****2468",
                sourceAccountNumber = "****5678",
                referenceNumber = null,
                failureReason = null,
                createdDate = "Dec 15, 2024 16:00 PM",
            ),
            AutoPayHistory(
                id = 7L,
                autoPayId = autoPayId,
                amount = 9.99,
                currency = "USD",
                status = PaymentStatus.CANCELLED,
                transactionDate = "Dec 10, 2024",
                recipientName = "Spotify",
                recipientAccountNumber = "****1357",
                sourceAccountNumber = "****5678",
                referenceNumber = null,
                failureReason = "Schedule cancelled by user",
                createdDate = "Dec 10, 2024 11:15 AM",
            ),
            AutoPayHistory(
                id = 8L,
                autoPayId = autoPayId,
                amount = 250.00,
                currency = "USD",
                status = PaymentStatus.PROCESSING,
                transactionDate = "Jan 20, 2023",
                recipientName = "Insurance Company",
                recipientAccountNumber = "****9753",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001239",
                failureReason = null,
                createdDate = "Jan 20, 2023 12:00 PM",
            ),
            AutoPayHistory(
                id = 9L,
                autoPayId = autoPayId,
                amount = 75.25,
                currency = "USD",
                status = PaymentStatus.PENDING,
                transactionDate = "Jan 18, 2023",
                recipientName = "Water Department",
                recipientAccountNumber = "****8642",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001240",
                failureReason = null,
                createdDate = "Jan 18, 2023 15:30 PM",
            ),
            AutoPayHistory(
                id = 10L,
                autoPayId = autoPayId,
                amount = 199.99,
                currency = "USD",
                status = PaymentStatus.COMPLETED,
                transactionDate = "Jan 12, 2023",
                recipientName = "Credit Card Payment",
                recipientAccountNumber = "****7531",
                sourceAccountNumber = "****5678",
                referenceNumber = "REF001241",
                failureReason = null,
                createdDate = "Jan 12, 2023 13:45 PM",
            ),
        )
    }

    private fun createDummyStatistics(): AutoPayHistoryStatistics {
        return AutoPayHistoryStatistics(
            totalTransactions = 10,
            successfulTransactions = 6,
            failedTransactions = 1,
            pendingTransactions = 1,
            totalAmount = 2128.46,
            successfulAmount = 1476.48,
            failedAmount = 156.75,
            currency = "USD",
        )
    }
}

data class AutoPayHistoryUiState(
    val isLoading: Boolean = false,
    val historyList: List<AutoPayHistory> = emptyList(),
    val filteredHistoryList: List<AutoPayHistory> = emptyList(),
    val historyPage: Page<AutoPayHistory>? = null,
    val statistics: AutoPayHistoryStatistics? = null,
    val error: String? = null,
    val isReadOnly: Boolean = true,
) {
    val displayHistoryList: List<AutoPayHistory>
        get() = filteredHistoryList.ifEmpty { historyList }
}
