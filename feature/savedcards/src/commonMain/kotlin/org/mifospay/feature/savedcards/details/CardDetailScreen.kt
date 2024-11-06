/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.savedcards.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.savedcards.generated.resources.Res
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_error_oops
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_loading
import mobile_wallet.feature.savedcards.generated.resources.feature_savedcards_subtitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.savedcards.SavedCard
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.savedcards.components.CreditCard
import org.mifospay.feature.savedcards.utils.CardMaskStyle
import org.mifospay.feature.savedcards.utils.CreditCardUtils.maskCreditCardNumber

@Composable
internal fun CardDetailScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardDetailViewModel = koinViewModel(),
) {
    val state by viewModel.cartDetailState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            CardDetailEvent.OnNavigateBack -> navigateBack.invoke()
        }
    }

    CardDetailScreen(
        state = state,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
        modifier = modifier,
    )
}

@Composable
internal fun CardDetailScreen(
    state: ViewState,
    modifier: Modifier = Modifier,
    onAction: (CardDetailAction) -> Unit,
) {
    MifosScaffold(
        topBarTitle = "Card Details",
        backPress = {
            onAction(CardDetailAction.NavigateBack)
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is ViewState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_savedcards_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_savedcards_error_oops),
                        subTitle = stringResource(Res.string.feature_savedcards_subtitle),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is ViewState.Content -> {
                    CardDetailScreenContent(
                        state = state,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun CardDetailScreenContent(
    state: ViewState.Content,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            var isCardNumberVisible by remember { mutableStateOf(false) }

            val maskStyle by remember(isCardNumberVisible) {
                mutableStateOf(
                    if (isCardNumberVisible) {
                        CardMaskStyle.SHOW_ALL
                    } else {
                        CardMaskStyle.ALL_EXCEPT_LAST_FOUR
                    },
                )
            }

            val formattedCvv by remember(isCardNumberVisible) {
                mutableStateOf(
                    if (isCardNumberVisible) state.savedCard.cvv else state.savedCard.maskedCvv,
                )
            }

            CreditCard(
                cardNumber = state.savedCard.cardNumber,
                fullName = state.savedCard.fullName,
                expiryDate = state.savedCard.formattedExpiryDate,
                cvv = formattedCvv,
                baseColor = state.selectedColor,
                maskStyle = maskStyle,
                onClick = {
                    isCardNumberVisible = !isCardNumberVisible
                },
            )
        }

        item {
            CardDetail(
                savedCard = state.savedCard,
            )
        }
    }
}

@Composable
private fun CardDetail(
    savedCard: SavedCard,
    modifier: Modifier = Modifier,
    containerColor: Color = NewUi.containerColor,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CreditCardLabelAndText(
                label = "Card Holder",
                text = savedCard.fullName,
            )
            MifosDivider()
            CreditCardLabelAndText(
                label = "Card Number",
                text = savedCard.cardNumber.maskCreditCardNumber(),
            )
            MifosDivider()
            CreditCardLabelAndText(
                label = "Expiry Date",
                text = savedCard.formattedExpiryDate,
            )
            MifosDivider()
            CreditCardLabelAndText(
                label = "CVV",
                text = savedCard.maskedCvv,
            )
        }
    }
}

@Composable
private fun CreditCardLabelAndText(
    label: String,
    text: String,
) {
    Column(
        modifier = Modifier
            .wrapContentSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                letterSpacing = 1.sp,
            ),
            color = Color.Black,
        )
    }
}
