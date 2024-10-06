/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.socialSignup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.auth.generated.resources.Res
import mobile_wallet.feature.auth.generated.resources.feature_auth_create_an_account
import mobile_wallet.feature.auth.generated.resources.feature_auth_or
import mobile_wallet.feature.auth.generated.resources.feature_auth_sign_up_as_customer
import mobile_wallet.feature.auth.generated.resources.feature_auth_sign_up_as_merchant
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.data.util.Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopAppBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
internal fun SignupMethodScreen(
    modifier: Modifier = Modifier,
    navigateToSignupScreen: (savingsProductId: Int) -> Unit = {},
    onDismissSignUp: () -> Unit = {},
) {
    SignupMethodScreenContent(
        modifier = modifier,
        onDismissSignUp = onDismissSignUp,
        navigateToSignupScreen = navigateToSignupScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignupMethodScreenContent(
    modifier: Modifier = Modifier,
    onDismissSignUp: () -> Unit = {},
    navigateToSignupScreen: (savingsProductId: Int) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopAppBar(
                title = "Choose Account Type",
                navigationIcon = MifosIcons.Back,
                onNavigationIconClick = onDismissSignUp,
                navigationIconContentDescription = "Back",
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        SignupMethodScreenContent(
            modifier = Modifier.padding(it),
            onSignUpAsMerchant = {
                navigateToSignupScreen(WALLET_ACCOUNT_SAVINGS_PRODUCT_ID)
            },
            onSignupAsCustomer = {
                navigateToSignupScreen(WALLET_ACCOUNT_SAVINGS_PRODUCT_ID)
            },
        )
    }
}

@Composable
private fun SignupMethodScreenContent(
    onSignUpAsMerchant: () -> Unit,
    onSignupAsCustomer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(Res.string.feature_auth_create_an_account),
        )

        MifosOutlinedButton(
            modifier = Modifier.padding(top = 48.dp),
            onClick = onSignUpAsMerchant,
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_sign_up_as_merchant).uppercase(),
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .padding(start = 24.dp, end = 8.dp)
                    .weight(.4f),
                thickness = 1.dp,
            )
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(.1f),
                text = stringResource(Res.string.feature_auth_or),
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(start = 8.dp, end = 24.dp)
                    .weight(.4f),
                thickness = 1.dp,
            )
        }

        MifosOutlinedButton(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onSignupAsCustomer,
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                text = stringResource(Res.string.feature_auth_sign_up_as_customer).uppercase(),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview
@Composable
private fun SignupMethodContentScreenPreview() {
    MifosTheme {
        SignupMethodScreenContent(
            onSignUpAsMerchant = {},
            onSignupAsCustomer = {},
        )
    }
}
