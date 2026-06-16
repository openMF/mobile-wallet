/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.mifospay.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.theme.darkKptColorScheme
import org.mifospay.core.designsystem.theme.lightKptColorScheme
import org.mifospay.core.model.widget.WidgetData
import org.mifospay.shared.widget.WidgetDataProvider
import org.mifospay.shared.widget.WidgetState
import org.mifospay.widget.ui.AddExpenseAction
import org.mifospay.widget.ui.AddIncomeAction
import org.mifospay.widget.ui.OpenDashboardAction

// ─── KptTheme-aligned ColorProviders ──────────────────────────────────────
// Uses the same lightKptColorScheme / darkKptColorScheme that MifosTheme uses,
// so the widget always stays in sync with the app's colour palette.

private val L = lightKptColorScheme
private val D = darkKptColorScheme

private val cpPrimary          = ColorProvider(day = L.primary,               night = D.primary)
private val cpOnPrimary        = ColorProvider(day = L.onPrimary,             night = D.onPrimary)
private val cpSurface          = ColorProvider(day = L.surface,               night = D.surface)
private val cpOnSurface        = ColorProvider(day = L.onSurface,             night = D.onSurface)
private val cpOnSurfaceVariant = ColorProvider(day = L.onSurfaceVariant,      night = D.onSurfaceVariant)
private val cpContainer        = ColorProvider(day = L.surfaceContainerHigh,  night = D.surfaceContainerHigh)
private val cpOutlineVariant   = ColorProvider(day = L.outlineVariant,        night = D.outlineVariant)
private val cpPrimaryContainer    = ColorProvider(day = L.primaryContainer,   night = D.primaryContainer)
private val cpOnPrimaryContainer  = ColorProvider(day = L.onPrimaryContainer, night = D.onPrimaryContainer)
private val cpErrorContainer      = ColorProvider(day = L.errorContainer,     night = D.errorContainer)
private val cpOnErrorContainer    = ColorProvider(day = L.onErrorContainer,   night = D.onErrorContainer)

// ──────────────────────────────────────────────────────────────────────────

object FinanceGlanceWidget : GlanceAppWidget(), KoinComponent {

    private val widgetDataProvider: WidgetDataProvider by inject()

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state by widgetDataProvider.widgetStateFlow
                .collectAsState(initial = WidgetState.Unauthenticated)

            val width = LocalSize.current.width
            when (state) {
                is WidgetState.Unauthenticated -> UnauthenticatedWidget()
                is WidgetState.Error -> ErrorWidget()
                is WidgetState.Authenticated -> {
                    val data = (state as WidgetState.Authenticated).data
                    if (width >= 250.dp) FullWidget(data) else CompactWidget(data)
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun UnauthenticatedWidget() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cpSurface)
            .cornerRadius(24.dp)
            .padding(20.dp),
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
        ) {

            Text(
                text = "MifosPay",
                style = TextStyle(
                    color = cpPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(GlanceModifier.height(2.dp))

            Text(
                text = "Digital Wallet",
                style = TextStyle(
                    color = cpOnSurfaceVariant,
                    fontSize = 10.sp,
                ),
            )

            Spacer(GlanceModifier.height(20.dp))

            Text(
                text = "Welcome Back",
                style = TextStyle(
                    color = cpOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(GlanceModifier.height(4.dp))

            Text(
                text = "Sign in to view balances and manage your wallet.",
                style = TextStyle(
                    color = cpOnSurfaceVariant,
                    fontSize = 11.sp,
                ),
            )

            Spacer(GlanceModifier.defaultWeight())

            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(cpPrimary)
                    .cornerRadius(14.dp)
                    .clickable(actionRunCallback<OpenDashboardAction>()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Sign In",
                    style = TextStyle(
                        color = cpOnPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}
@SuppressLint("RestrictedApi")
@Composable
private fun ErrorWidget() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cpSurface)
            .cornerRadius(24.dp)
            .padding(20.dp),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Text(
                text = "MifosPay",
                style = TextStyle(color = cpPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = "Digital Wallet",
                style = TextStyle(color = cpOnSurfaceVariant, fontSize = 10.sp),
            )
            Spacer(GlanceModifier.height(20.dp))
            Text(
                text = "Failed to fetch balance",
                style = TextStyle(color = cpOnSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "Tap to retry or open the app.",
                style = TextStyle(color = cpOnSurfaceVariant, fontSize = 11.sp),
            )
            Spacer(GlanceModifier.defaultWeight())
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(cpPrimary)
                    .cornerRadius(14.dp)
                    .clickable(actionRunCallback<OpenDashboardAction>()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Open App",
                    style = TextStyle(color = cpOnPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun FullWidget(data: WidgetData) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cpSurface)
            .cornerRadius(24.dp)
            .clickable(actionRunCallback<OpenDashboardAction>()),
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {

            // ───────────── Header ─────────────

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Column(
                    modifier = GlanceModifier.defaultWeight(),
                ) {

                    Text(
                        text = "MifosPay",
                        style = TextStyle(
                            color = cpPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )

                    Text(
                        text = "Digital Wallet",
                        style = TextStyle(
                            color = cpOnSurfaceVariant,
                            fontSize = 10.sp,
                        ),
                    )
                }

                if (data.accountNumber.isNotBlank()) {
                    Box(
                        modifier = GlanceModifier
                            .background(cpPrimaryContainer)
                            .cornerRadius(100.dp)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "••${data.accountNumber.takeLast(4)}",
                            style = TextStyle(
                                color = cpOnPrimaryContainer,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }

            Spacer(GlanceModifier.height(14.dp))

            // ───────────── Balance ─────────────

            Text(
                text = "Available Balance",
                style = TextStyle(
                    color = cpOnSurfaceVariant,
                    fontSize = 11.sp,
                ),
            )

            Spacer(GlanceModifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {

                Text(
                    text = data.currency,
                    style = TextStyle(
                        color = cpPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(GlanceModifier.width(6.dp))

                Text(
                    text = CurrencyFormatter.format(data.currentBalance, 2),
                    style = TextStyle(
                        color = cpOnSurface,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            Spacer(GlanceModifier.height(4.dp))

            Text(
                text = "Wallet Active",
                style = TextStyle(
                    color = cpPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )

            Spacer(GlanceModifier.height(14.dp))

            // ───────────── Actions ─────────────

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
            ) {

                PremiumActionButton(
                    label = "+ Income",
                    fg = cpOnPrimaryContainer,
                    bg = cpPrimaryContainer,
                    action = actionRunCallback<AddIncomeAction>(),
                    modifier = GlanceModifier.defaultWeight(),
                )

                Spacer(GlanceModifier.width(8.dp))

                PremiumActionButton(
                    label = "− Expense",
                    fg = cpOnErrorContainer,
                    bg = cpErrorContainer,
                    action = actionRunCallback<AddExpenseAction>(),
                    modifier = GlanceModifier.defaultWeight(),
                )
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun PremiumActionButton(
    label: String,
    fg: ColorProvider,
    bg: ColorProvider,
    action: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .background(bg)
            .cornerRadius(14.dp)
            .clickable(action),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = fg,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
@SuppressLint("RestrictedApi")
@Composable
private fun CompactWidget(data: WidgetData) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(cpSurface)
            .cornerRadius(24.dp)
            .clickable(actionRunCallback<OpenDashboardAction>())
            .padding(14.dp),
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
        ) {

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = "MifosPay",
                    style = TextStyle(
                        color = cpPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = GlanceModifier.defaultWeight(),
                )

                if (data.accountNumber.isNotBlank()) {
                    Box(
                        modifier = GlanceModifier
                            .background(cpPrimaryContainer)
                            .cornerRadius(100.dp)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "••${data.accountNumber.takeLast(4)}",
                            style = TextStyle(
                                color = cpOnPrimaryContainer,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }

            Spacer(GlanceModifier.height(10.dp))

            Text(
                text = "Available Balance",
                style = TextStyle(
                    color = cpOnSurfaceVariant,
                    fontSize = 9.sp,
                ),
            )

            Spacer(GlanceModifier.height(2.dp))

            Text(
                text = data.currency,
                style = TextStyle(
                    color = cpPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(GlanceModifier.height(2.dp))

            Text(
                text = CurrencyFormatter.format(data.currentBalance, 2),
                style = TextStyle(
                    color = cpOnSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
private fun ChipButton(
    label: String,
    fg: ColorProvider,
    bg: ColorProvider,
    action: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        modifier = modifier.height(34.dp).background(bg).cornerRadius(10.dp).clickable(action),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = TextStyle(color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium))
    }
}

