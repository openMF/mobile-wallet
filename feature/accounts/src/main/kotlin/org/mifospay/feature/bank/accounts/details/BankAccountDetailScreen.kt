/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts.details

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.feature.bank.accounts.R

@Composable
internal fun BankAccountDetailScreen(
    bankAccountDetails: BankAccountDetails,
    onSetupUpiPin: () -> Unit,
    onChangeUpiPin: () -> Unit,
    onForgotUpiPin: () -> Unit,
    navigateBack: () -> Unit,
) {
    BankAccountDetailScreen(
        bankName = bankAccountDetails.bankName.toString(),
        accountHolderName = bankAccountDetails.accountholderName.toString(),
        branchName = bankAccountDetails.branch.toString(),
        ifsc = bankAccountDetails.ifsc.toString(),
        type = bankAccountDetails.type.toString(),
        isUpiEnabled = bankAccountDetails.isUpiEnabled,
        onSetupUpiPin = onSetupUpiPin,
        onChangeUpiPin = onChangeUpiPin,
        onForgotUpiPin = onForgotUpiPin,
        navigateBack = navigateBack,
    )
}

@Composable
private fun BankAccountDetailScreen(
    bankName: String,
    accountHolderName: String,
    branchName: String,
    ifsc: String,
    type: String,
    isUpiEnabled: Boolean,
    onSetupUpiPin: () -> Unit,
    onChangeUpiPin: () -> Unit,
    onForgotUpiPin: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        MifosTopBar(
            topBarTitle = R.string.feature_accounts_bank_account_details,
            backPress = navigateBack,
        )

        Column(
            modifier = Modifier
                .padding(20.dp)
                .border(2.dp, MaterialTheme.colorScheme.onSurface)
                .padding(20.dp),
        ) {
            BankAccountDetailRows(
                modifier = Modifier.fillMaxWidth(),
                detail = R.string.feature_accounts_bank_name,
                detailValue = bankName,
            )
            BankAccountDetailRows(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                detail = R.string.feature_accounts_ac_holder_name,
                detailValue = accountHolderName,
            )
            BankAccountDetailRows(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                detail = R.string.feature_accounts_branch_name,
                detailValue = branchName,
            )
            BankAccountDetailRows(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                detail = R.string.feature_accounts_ifsc,
                detailValue = ifsc,
            )
            BankAccountDetailRows(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                detail = R.string.feature_accounts_type,
                detailValue = type,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BankAccountDetailButton(
                btnText = R.string.feature_accounts_setup_upi,
                onClick = { onSetupUpiPin.invoke() },
                isUpiEnabled = !isUpiEnabled,
                hasTrailingIcon = false,
            )

            BankAccountDetailButton(
                btnText = R.string.feature_accounts_delete_bank,
                onClick = {},
                isUpiEnabled = !isUpiEnabled,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            BankAccountDetailButton(
                btnText = R.string.feature_accounts_change_upi_pin,
                onClick = { onChangeUpiPin.invoke() },
                isUpiEnabled = isUpiEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
            BankAccountDetailButton(
                btnText = R.string.feature_accounts_forgot_upi_pin,
                onClick = { onForgotUpiPin.invoke() },
                isUpiEnabled = isUpiEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BankAccountDetailRows(
    detail: Int,
    detailValue: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = detail),
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = detailValue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BankAccountDetailButton(
    btnText: Int,
    onClick: () -> Unit,
    isUpiEnabled: Boolean,
    modifier: Modifier = Modifier,
    hasTrailingIcon: Boolean = false,
) {
    if (isUpiEnabled) {
        MifosButton(
            onClick = { onClick.invoke() },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
            modifier = modifier
                .padding(start = 20.dp, end = 20.dp),
            contentPadding = PaddingValues(20.dp),
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = btnText),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                if (hasTrailingIcon) {
                    Icon(
                        imageVector = MifosIcons.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BankAccountDetailUpiDisabledPreview() {
    BankAccountDetailScreen(
        "Mifos Bank",
        "Mifos Account Holder",
        "Mifos Branch",
        "IFSC",
        "type",
        false,
        {}, {}, {}, {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BankAccountDetailUpiEnabledPreview() {
    BankAccountDetailScreen(
        "Mifos Bank",
        "Mifos Account Holder",
        "Mifos Branch",
        "IFSC",
        "type",
        true,
        {}, {}, {}, {},
    )
}
