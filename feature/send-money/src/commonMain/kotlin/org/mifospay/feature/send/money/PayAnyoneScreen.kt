/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.AvatarBox
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PayAnyoneScreen(
    onBackClick: () -> Unit,
    onContactPickerClick: () -> Unit,
    onContactSelected: (String) -> Unit = {},
    selectedContactPhone: String? = null,
    modifier: Modifier = Modifier,
) {
    val viewModel: PayAnyoneViewModel = remember { PayAnyoneViewModel(SavedStateHandle()) }
    val state by viewModel.stateFlow.collectAsState()

    var currentPlaceholderIndex by remember { mutableStateOf(0) }

    val placeholderMessages = listOf(
        "Enter UPI ID or number",
        "Enter phone number or name",
    )

    val currentPlaceholder = placeholderMessages[currentPlaceholderIndex]

    val keyboardType = if (state.isKeyboardNumeric) {
        KeyboardType.Number
    } else {
        KeyboardType.Text
    }

    val keyboardToggleText = if (state.isKeyboardNumeric) "ABC" else "123"

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentPlaceholderIndex = (currentPlaceholderIndex + 1) % placeholderMessages.size
        }
    }

    LaunchedEffect(selectedContactPhone) {
        selectedContactPhone?.let { phoneNumber ->
            viewModel.trySendAction(PayAnyoneAction.PhoneNumberSelected(phoneNumber))
        }
    }

    MifosGradientBackground {
        MifosScaffold(
            modifier = modifier,
            topBar = {
                MifosTopBar(
                    topBarTitle = "Pay Anyone",
                    backPress = onBackClick,
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(horizontal = KptTheme.spacing.lg),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                    Text(
                        text = "Pay any UPI app using name, number or UPI ID",
                        style = KptTheme.typography.titleSmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

                    MifosOutlinedTextField(
                        value = state.inputValue,
                        onValueChange = {
                            viewModel.trySendAction(PayAnyoneAction.InputValueChanged(it))
                        },
                        label = "",
                        placeholder = {
                            AnimatedContent(
                                targetState = currentPlaceholder,
                                transitionSpec = {
                                    slideInVertically(
                                        animationSpec = tween(500),
                                        initialOffsetY = { fullHeight -> fullHeight },
                                    ) + fadeIn(animationSpec = tween(500)) togetherWith
                                        slideOutVertically(
                                            animationSpec = tween(500),
                                            targetOffsetY = { fullHeight -> -fullHeight },
                                        ) + fadeOut(animationSpec = tween(500))
                                },
                            ) { placeholder ->
                                Text(
                                    text = placeholder,
                                    style = KptTheme.typography.bodyMedium,
                                    color = KptTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            imeAction = ImeAction.Search,
                        ),
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AnimatedContent(
                                    targetState = state.showClearIcon,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(200)) togetherWith
                                            fadeOut(animationSpec = tween(200))
                                    },
                                ) { showClear ->
                                    if (showClear) {
                                        IconButton(
                                            onClick = {
                                                viewModel.trySendAction(PayAnyoneAction.ClearInput)
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                contentColor = KptTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            ),
                                        ) {
                                            Icon(
                                                imageVector = MifosIcons.Close,
                                                contentDescription = "Clear input",
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    } else {
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    viewModel.trySendAction(PayAnyoneAction.ToggleKeyboardType)
                                                },
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    contentColor = KptTheme.colorScheme.primary,
                                                ),
                                            ) {
                                                Text(
                                                    text = keyboardToggleText,
                                                    style = KptTheme.typography.labelMedium,
                                                    color = KptTheme.colorScheme.primary,
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(KptTheme.spacing.xs))

                                            IconButton(
                                                onClick = onContactPickerClick,
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    contentColor = KptTheme.colorScheme.primary,
                                                ),
                                            ) {
                                                Icon(
                                                    imageVector = MifosIcons.Contact,
                                                    contentDescription = "Select from contacts",
                                                    modifier = Modifier.size(20.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    )
                }

                Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                ) {
                    if (state.recentContacts.isNotEmpty()) {
                        item {
                            RecentContactsSection(
                                recentContacts = state.recentContacts,
                                onContactSelected = { contact ->
                                    viewModel.trySendAction(PayAnyoneAction.PhoneNumberSelected(contact.phoneNumber))
                                },
                            )
                        }
                    }

                    if (state.allContacts.isNotEmpty()) {
                        item {
                            Text(
                                text = "All People on UPI",
                                style = KptTheme.typography.titleMedium,
                                color = KptTheme.colorScheme.onSurface,
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
                        }

                        items(
                            items = state.allContacts,
                            key = { contact -> "all_${contact.id}_${contact.phoneNumber}" },
                        ) { contact ->
                            AllPeopleContactItem(
                                contact = contact,
                                onClick = {
                                    viewModel.trySendAction(PayAnyoneAction.PhoneNumberSelected(contact.phoneNumber))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentContactsSection(
    recentContacts: List<Contact>,
    onContactSelected: (Contact) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Recent",
            style = KptTheme.typography.titleMedium,
            color = KptTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

        Column(
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        ) {
            recentContacts.forEach { contact ->
                RecentContactCard(
                    contact = contact,
                    onClick = { onContactSelected(contact) },
                )
            }
        }
    }
}

@Composable
private fun RecentContactCard(
    contact: Contact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = KptTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarBox(
                name = contact.name,
                size = 48,
                backgroundColor = KptTheme.colorScheme.primaryContainer,
            )

            Spacer(modifier = Modifier.width(KptTheme.spacing.sm))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = contact.name,
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (contact.upiId != null) {
                    Text(
                        text = contact.upiId,
                        style = KptTheme.typography.bodySmall,
                        color = KptTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun AllPeopleContactItem(
    contact: Contact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = KptTheme.shapes.small,
        colors = CardDefaults.outlinedCardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = contact.name,
                    style = KptTheme.typography.bodyLarge,
                    color = KptTheme.colorScheme.onSurface,
                )
            },
            supportingContent = {
                Column {
                    Text(
                        text = contact.phoneNumber,
                        style = KptTheme.typography.bodyMedium,
                        color = KptTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    if (contact.upiId != null) {
                        Text(
                            text = contact.upiId,
                            style = KptTheme.typography.bodySmall,
                            color = KptTheme.colorScheme.primary,
                        )
                    }
                }
            },
            leadingContent = {
                AvatarBox(
                    name = contact.name,
                    backgroundColor = KptTheme.colorScheme.primaryContainer,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = KptTheme.colorScheme.surface,
            ),
        )
    }
}

@Preview
@Composable
fun PayAnyoneScreenPreview() {
    PayAnyoneScreen(
        onBackClick = {},
        onContactPickerClick = {},
        onContactSelected = {},
        selectedContactPhone = null,
    )
}
