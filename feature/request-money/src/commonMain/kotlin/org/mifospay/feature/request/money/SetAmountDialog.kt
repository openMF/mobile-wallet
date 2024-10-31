/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.request_money.generated.resources.Res
import mobile_wallet.feature.request_money.generated.resources.feature_request_money_cancel
import mobile_wallet.feature.request_money.generated.resources.feature_request_money_confirm
import mobile_wallet.feature.request_money.generated.resources.feature_request_money_currency
import mobile_wallet.feature.request_money.generated.resources.feature_request_money_set_amount
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosCustomDialog
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.utils.onClick
import org.mifospay.core.model.utils.CurrencyCode
import org.mifospay.core.model.utils.filterList
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.DropdownBox
import org.mifospay.core.ui.DropdownBoxItem
import org.mifospay.core.ui.MifosDivider

@Composable
internal fun SetAmountDialog(
    currency: String,
    amount: String,
    currencyList: List<CurrencyCode>,
    modifier: Modifier = Modifier,
    onAction: (ShowQrAction) -> Unit,
) {
    MifosCustomDialog(
        onDismiss = {
            onAction(ShowQrAction.DismissDialog)
        },
        content = {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_request_money_set_amount),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    val amountValidator by remember(amount) {
                        derivedStateOf {
                            when {
                                amount.trim() == "" -> null

                                amount.trim().any { it.isLetter() } -> {
                                    "Please enter a valid amount"
                                }

                                amount.trim().toDoubleOrNull() == null -> {
                                    "Please enter a valid amount"
                                }

                                amount.trim().toDouble().compareTo(0.0) <= 0 -> {
                                    "Please enter a valid amount"
                                }

                                else -> null
                            }
                        }
                    }

                    MifosTextField(
                        value = amount,
                        label = stringResource(Res.string.feature_request_money_set_amount),
                        onValueChange = {
                            onAction(ShowQrAction.AmountChanged(it))
                        },
                        isError = amountValidator != null,
                        errorText = amountValidator,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                    )

                    val filteredCurrencyList by remember(currencyList, currency) {
                        derivedStateOf {
                            currencyList.filterList(currency)
                        }
                    }

                    var currencyToggled by remember { mutableStateOf(false) }

                    DropdownBox(
                        expanded = currencyToggled,
                        label = stringResource(Res.string.feature_request_money_currency),
                        value = currency,
                        readOnly = false,
                        onValueChange = {
                            onAction(ShowQrAction.CurrencyChanged(it))
                        },
                        onExpandChange = {
                            currencyToggled = it
                        },
                    ) {
                        filteredCurrencyList.forEachIndexed { index, currencyCode ->
                            CurrencyDropdownItem(
                                currency = currencyCode,
                                onClick = {
                                    onAction(ShowQrAction.CurrencyChanged(it))
                                    currencyToggled = false
                                },
                            )

                            if (index < filteredCurrencyList.size - 1) {
                                MifosDivider()
                            }
                        }

                        if (filteredCurrencyList.isEmpty()) {
                            DropdownBoxItem(
                                text = "No currency found",
                                onClick = {
                                    currencyToggled = false
                                },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        MifosOutlinedButton(
                            onClick = {
                                onAction(ShowQrAction.DismissDialog)
                            },
                        ) {
                            Text(text = stringResource(Res.string.feature_request_money_cancel))
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        MifosButton(
                            onClick = {
                                if (amountValidator == null) {
                                    onAction(ShowQrAction.ConfirmSetAmount)
                                }
                            },
                        ) {
                            Text(text = stringResource(Res.string.feature_request_money_confirm))
                        }
                    }
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun CurrencyDropdownItem(
    currency: CurrencyCode,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = currency.countryName)
        },
        leadingContent = {
            AvatarBox(
                name = currency.currencySymbol,
            )
        },
        trailingContent = {
            Text(text = currency.currencyCode)
        },
        modifier = modifier
            .fillMaxWidth()
            .onClick { onClick(currency.currencyCode) },
    )
}

@Preview
@Composable
private fun SetAmountDialogPreview() {
    SetAmountDialog(
        amount = "",
        currency = "",
        currencyList = emptyList(),
        onAction = { },
    )
}
