/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repository

import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.model.autopay.AutoPay
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.model.autopay.AutoPayPayload
import org.mifospay.core.model.autopay.AutoPayTemplate
import org.mifospay.core.model.autopay.AutoPayUpdatePayload
import org.mifospay.core.model.autopay.UpcomingPayment
import org.mifospay.core.network.model.entity.Page

interface AutoPayRepository {
    // Phase-3 cutover — Flow-shaped reads on ScreenState.

    /**
     * Get AutoPay template for creating new AutoPay schedules
     */
    fun getAutoPayTemplate(
        clientId: Long,
        sourceAccountId: Long,
    ): ScreenStateStream<AutoPayTemplate>

    /**
     * Get all AutoPay schedules for a client
     */
    fun getAllAutoPaySchedules(
        clientId: Long,
    ): ScreenStateStream<List<AutoPay>>

    /**
     * Get AutoPay schedule by ID
     */
    fun getAutoPaySchedule(autoPayId: Long): ScreenStateStream<AutoPay>

    // Writes stay on DataState (Phase-3 D1).

    /**
     * Create a new AutoPay schedule
     */
    suspend fun createAutoPaySchedule(
        payload: AutoPayPayload,
    ): DataState<String>

    /**
     * Update an existing AutoPay schedule
     */
    suspend fun updateAutoPaySchedule(
        autoPayId: Long,
        payload: AutoPayUpdatePayload,
    ): DataState<String>

    /**
     * Delete an AutoPay schedule
     */
    suspend fun deleteAutoPaySchedule(autoPayId: Long): DataState<String>

    /**
     * Pause an AutoPay schedule
     */
    suspend fun pauseAutoPaySchedule(autoPayId: Long): DataState<String>

    /**
     * Resume a paused AutoPay schedule
     */
    suspend fun resumeAutoPaySchedule(autoPayId: Long): DataState<String>

    /**
     * Get AutoPay payment history. Keeps the [Page] wrapper inside Content
     * (mirrors `AutoPayHistoryRepository.getAutoPayHistoryWithPagination`
     * treatment).
     */
    fun getAutoPayHistory(
        autoPayId: Long,
        limit: Int = 20,
    ): ScreenStateStream<Page<AutoPayHistory>>

    /**
     * Get upcoming payments for all AutoPay schedules
     */
    fun getUpcomingPayments(
        clientId: Long,
        limit: Int = 10,
    ): ScreenStateStream<List<UpcomingPayment>>

    /**
     * Get AutoPay statistics for dashboard
     */
    fun getAutoPayStatistics(
        clientId: Long,
    ): ScreenStateStream<AutoPayStatistics>

    /**
     * Validate AutoPay payload before submission (pure-local validation,
     * no I/O; stays on DataState).
     */
    suspend fun validateAutoPayPayload(payload: AutoPayPayload): DataState<Boolean>
}

data class AutoPayStatistics(
    val totalActiveSchedules: Int = 0,
    val totalPausedSchedules: Int = 0,
    val totalCompletedSchedules: Int = 0,
    val totalUpcomingPayments: Int = 0,
    val totalAmountThisMonth: Double = 0.0,
    val currency: String = "USD",
)
