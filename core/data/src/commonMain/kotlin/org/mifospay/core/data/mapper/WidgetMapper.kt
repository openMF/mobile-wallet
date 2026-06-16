package org.mifospay.core.data.mapper

import org.mifospay.core.model.widget.WidgetData
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Mapper that converts your existing financial domain objects into [WidgetData].
 *
 * Lives in :core:data/mapper — same home as other mappers in the project.
 *
 * Replace the field names below with your actual Account / Balance / Budget
 * domain classes. The mapper keeps [WidgetData] decoupled from your financial
 * domain — it only knows what the widget needs to render.
 *
 * Example call from a ViewModel or UseCase:
 *
 *   val widgetData = WidgetDataMapper.from(
 *       balance       = account.balance,
 *       currency      = account.currency,
 *       budgetTotal   = budget.totalAmount,
 *       budgetSpent   = budget.spentAmount,
 *       lastIncome    = lastTransaction.takeIf { it.isCredit }?.amount ?: 0.0,
 *       lastExpense   = lastTransaction.takeIf { it.isDebit }?.amount ?: 0.0,
 *       userName      = client.displayName,
 *   )
 */
object WidgetDataMapper {
    @OptIn(ExperimentalTime::class)
    fun from(
        balance: Double,
        currency: String,
        budgetTotal: Double,
        budgetSpent: Double,
        lastIncome: Double,
        lastExpense: Double,
        userName: String,
        timestampMs: Long = Clock.System.now().toEpochMilliseconds(),
    ): WidgetData = WidgetData(
        currentBalance = balance,
        currency       = currency,
        budgetTotal    = budgetTotal,
        budgetSpent    = budgetSpent,
        lastIncome     = lastIncome,
        lastExpense    = lastExpense,
        lastUpdatedMs  = timestampMs,
        userName       = userName,
    )
}