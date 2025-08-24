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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.rememberMifosPullToRefreshState
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.utils.EventsEffect

@Composable
fun AutoPayScreen(
    onNavigateToSetup: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToScheduleDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AutoPayViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val pullRefreshState = rememberMifosPullToRefreshState(
        isEnabled = true,
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.trySendAction(AutoPayAction.RefreshDashboard) },
    )

    MifosScaffold(
        modifier = modifier,
        topBarTitle = "AutoPay Dashboard",
        backPress = { /* Handle back navigation */ },
        pullToRefreshState = pullRefreshState,
    ) { paddingValues ->
        if (state.isLoading && state.activeSchedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            AutoPayDashboardContent(
                state = state,
                onRefresh = { viewModel.trySendAction(AutoPayAction.RefreshDashboard) },
                onAddNewSchedule = { viewModel.trySendAction(AutoPayAction.AddNewSchedule) },
                onManageSchedules = { viewModel.trySendAction(AutoPayAction.ManageExistingSchedules) },
                onViewScheduleDetails = { scheduleId ->
                    viewModel.trySendAction(AutoPayAction.ViewScheduleDetails(scheduleId))
                },
                onNavigateToSetup = onNavigateToSetup,
                onNavigateToRules = onNavigateToRules,
                onNavigateToPreferences = onNavigateToPreferences,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToScheduleDetails = onNavigateToScheduleDetails,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }

    EventsEffect(viewModel) { event ->
        when (event) {
            is AutoPayEvent.NavigateToSetup -> onNavigateToSetup()
            is AutoPayEvent.NavigateToRules -> onNavigateToRules()
            is AutoPayEvent.NavigateToPreferences -> onNavigateToPreferences()
            is AutoPayEvent.NavigateToHistory -> onNavigateToHistory()
            is AutoPayEvent.NavigateToScheduleDetails -> onNavigateToScheduleDetails(event.scheduleId)
        }
    }
}

@Composable
private fun AutoPayDashboardContent(
    state: AutoPayState,
    onRefresh: () -> Unit,
    onAddNewSchedule: () -> Unit,
    onManageSchedules: () -> Unit,
    onViewScheduleDetails: (String) -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToRules: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToScheduleDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            DashboardHeader(
                totalActiveSchedules = state.totalActiveSchedules,
                totalUpcomingPayments = state.totalUpcomingPayments,
            )
        }

        item {
            QuickActionsSection(
                onAddNewSchedule = onAddNewSchedule,
                onManageSchedules = onManageSchedules,
            )
        }

        item {
            Text(
                text = "Active Schedules",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        if (state.activeSchedules.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No Active Schedules",
                    description = "You don't have any active AutoPay schedules. Create one to get started!",
                    icon = MifosIcons.Payment,
                )
            }
        } else {
            items(state.activeSchedules) { schedule ->
                ActiveScheduleCard(
                    schedule = schedule,
                    onClick = { onViewScheduleDetails(schedule.id) },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Upcoming Payments",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        if (state.upcomingPayments.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No Upcoming Payments",
                    description = "No payments are scheduled for the near future.",
                    icon = MifosIcons.CalenderMonth,
                )
            }
        } else {
            items(state.upcomingPayments) { payment ->
                UpcomingPaymentCard(
                    payment = payment,
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    totalActiveSchedules: Int,
    totalUpcomingPayments: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "AutoPay Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                DashboardStat(
                    label = "Active Schedules",
                    value = totalActiveSchedules.toString(),
                    icon = MifosIcons.Payment,
                )

                DashboardStat(
                    label = "Upcoming Payments",
                    value = totalUpcomingPayments.toString(),
                    icon = MifosIcons.CalenderMonth,
                )
            }
        }
    }
}

@Composable
private fun DashboardStat(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun QuickActionsSection(
    onAddNewSchedule: () -> Unit,
    onManageSchedules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                QuickActionButton(
                    text = "Add New",
                    icon = MifosIcons.Add,
                    onClick = onAddNewSchedule,
                    modifier = Modifier.weight(1f),
                )

                QuickActionButton(
                    text = "Manage",
                    icon = MifosIcons.Settings,
                    onClick = onManageSchedules,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = text)
    }
}

@Composable
private fun ActiveScheduleCard(
    schedule: AutoPaySchedule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = schedule.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = schedule.recipientName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    StatusChip(status = schedule.status)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = CurrencyFormatter.format(schedule.amount, schedule.currency, 2),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Column {
                        Text(
                            text = "Next Payment",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = schedule.nextPaymentDate,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Column {
                        Text(
                            text = "Frequency",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = schedule.frequency,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingPaymentCard(
    payment: UpcomingPayment,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = payment.scheduleName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = payment.recipientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                StatusChip(status = payment.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = CurrencyFormatter.format(payment.amount, payment.currency, 2),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column {
                    Text(
                        text = "Due Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = payment.dueDate,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    status: AutoPayStatus,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) = when (status) {
        AutoPayStatus.ACTIVE -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        AutoPayStatus.PAUSED -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        AutoPayStatus.CANCELLED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        AutoPayStatus.COMPLETED -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun StatusChip(
    status: PaymentStatus,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) = when (status) {
        PaymentStatus.UPCOMING -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        PaymentStatus.PROCESSING -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        PaymentStatus.COMPLETED -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        PaymentStatus.FAILED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
