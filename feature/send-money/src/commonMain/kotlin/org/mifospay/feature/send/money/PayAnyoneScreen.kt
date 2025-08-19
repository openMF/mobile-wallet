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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosOutlinedTextField
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PayAnyoneScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var inputValue by remember { mutableStateOf("") }
    var isKeyboardNumeric by remember { mutableStateOf(false) }
    var showClearIcon by remember { mutableStateOf(false) }
    var currentPlaceholderIndex by remember { mutableStateOf(0) }

    val placeholderMessages = listOf(
        "Enter UPI ID or number",
        "Enter phone number or name",
    )

    val currentPlaceholder = placeholderMessages[currentPlaceholderIndex]

    val keyboardType = if (isKeyboardNumeric) {
        KeyboardType.Number
    } else {
        KeyboardType.Text
    }

    val keyboardToggleText = if (isKeyboardNumeric) "ABC" else "123"

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentPlaceholderIndex = (currentPlaceholderIndex + 1) % placeholderMessages.size
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
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                Text(
                    text = "Pay any UPI app using name, number or UPI ID",
                    style = KptTheme.typography.titleSmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

                MifosOutlinedTextField(
                    value = inputValue,
                    onValueChange = {
                        inputValue = it
                        showClearIcon = it.isNotEmpty()
                    },
                    label = "",
                    placeholder = null,
                    leadingIcon = {
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
                                modifier = Modifier.padding(start = KptTheme.spacing.sm),
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AnimatedContent(
                                targetState = showClearIcon,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(200)) togetherWith
                                        fadeOut(animationSpec = tween(200))
                                },
                            ) { showClear ->
                                if (showClear) {
                                    IconButton(
                                        onClick = {
                                            inputValue = ""
                                            showClearIcon = false
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
                                                isKeyboardNumeric = !isKeyboardNumeric
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
                                            onClick = {
                                                // TODO: Implement contact picker functionality
                                            },
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
        }
    }
}
