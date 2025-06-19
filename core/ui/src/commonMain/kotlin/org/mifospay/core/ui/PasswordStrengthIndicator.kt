/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mobile_wallet.core.ui.generated.resources.Res
import mobile_wallet.core.ui.generated.resources.core_ui_error_icon_description
import mobile_wallet.core.ui.generated.resources.core_ui_password_requirements
import mobile_wallet.core.ui.generated.resources.core_ui_password_strength_labels
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun CombinedPasswordErrorCard(
    passwordStrengthState: PasswordStrengthState,
    currentCharacterCount: Int,
    modifier: Modifier = Modifier,
    errorText: StringResource? = null,
    errors: List<String> = emptyList(),
    minimumCharacterCount: Int? = null,
    isPasswordFieldFocused: Boolean = false,
) {
    val hasErrors = errorText != null || errors.isNotEmpty()

    val widthPercent by animateFloatAsState(
        targetValue = when (passwordStrengthState) {
            PasswordStrengthState.NONE -> 0f
            PasswordStrengthState.WEAK_1 -> .25f
            PasswordStrengthState.WEAK_2 -> .5f
            PasswordStrengthState.WEAK_3 -> .66f
            PasswordStrengthState.GOOD -> .82f
            PasswordStrengthState.STRONG -> 1f
            PasswordStrengthState.VERY_STRONG -> 1f
        },
        label = "Width Percent State",
    )

    val indicatorColor = when (passwordStrengthState) {
        PasswordStrengthState.NONE -> MaterialTheme.colorScheme.error
        PasswordStrengthState.WEAK_1 -> MaterialTheme.colorScheme.error
        PasswordStrengthState.WEAK_2 -> MaterialTheme.colorScheme.error
        PasswordStrengthState.WEAK_3 -> weakColor
        PasswordStrengthState.GOOD -> MaterialTheme.colorScheme.primary
        PasswordStrengthState.STRONG -> strongColor
        PasswordStrengthState.VERY_STRONG -> Color.Magenta
    }

    val animatedIndicatorColor by animateColorAsState(
        targetValue = indicatorColor,
        label = "Indicator Color State",
    )

    val strengthLabels = stringArrayResource(resource = Res.array.core_ui_password_strength_labels)
    val strengthLabel = strengthLabels[passwordStrengthState.ordinal]

    AnimatedVisibility(visible = hasErrors || isPasswordFieldFocused) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("passwordErrorCard"),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
            ),
        ) {
            Column {
                // Top border strength indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .graphicsLayer {
                                transformOrigin =
                                    TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0f)
                                scaleX = widthPercent
                            }
                            .drawBehind {
                                drawRect(animatedIndicatorColor)
                            },
                    )
                }

                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Header row with "Password Requirements" and strength label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = MifosIcons.OutlinedInfo,
                                contentDescription = stringResource(Res.string.core_ui_error_icon_description),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = stringResource(Res.string.core_ui_password_requirements),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        if (strengthLabel.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = animatedIndicatorColor,
                                        shape = RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = strengthLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }

                    // Minimum character count indicator if provided
                    minimumCharacterCount?.let { minCount ->
                        MinimumCharacterCount(
                            minimumRequirementMet = currentCharacterCount >= minCount,
                            minimumCharacterCount = minCount,
                        )
                    }

                    // Error text if provided
                    errorText?.let {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("passwordError"),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = CircleShape,
                                    )
                                    .padding(top = 6.dp),
                            )
                            Text(
                                text = stringResource(it),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // Error list
                    errors.forEachIndexed { index, error ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("passwordError_$index"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterHorizontally,
                            ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = CircleShape,
                                    )
                                    .padding(top = 6.dp),
                            )
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod", "MagicNumber")
@Composable
fun PasswordStrengthIndicator(
    modifier: Modifier = Modifier,
    state: PasswordStrengthState,
    currentCharacterCount: Int,
    minimumCharacterCount: Int? = null,
) {
    val widthPercent by animateFloatAsState(
        targetValue = when (state) {
            PasswordStrengthState.NONE -> 0f
            PasswordStrengthState.WEAK_1 -> .25f
            PasswordStrengthState.WEAK_2 -> .5f
            PasswordStrengthState.WEAK_3 -> .66f
            PasswordStrengthState.GOOD -> .82f
            PasswordStrengthState.STRONG -> 1f
            PasswordStrengthState.VERY_STRONG -> 1f
        },
        label = "Width Percent State",
    )
    val indicatorColor = when (state) {
        PasswordStrengthState.NONE -> MaterialTheme.colorScheme.error
        PasswordStrengthState.WEAK_1 -> MaterialTheme.colorScheme.error
        PasswordStrengthState.WEAK_2 -> MaterialTheme.colorScheme.error
        PasswordStrengthState.WEAK_3 -> weakColor
        PasswordStrengthState.GOOD -> MaterialTheme.colorScheme.primary
        PasswordStrengthState.STRONG -> strongColor
        PasswordStrengthState.VERY_STRONG -> Color.Magenta
    }
    val animatedIndicatorColor by animateColorAsState(
        targetValue = indicatorColor,
        label = "Indicator Color State",
    )

    val strengthLabels = stringArrayResource(resource = Res.array.core_ui_password_strength_labels)
    val strengthLabel = strengthLabels[state.ordinal]

    Column(
        modifier = modifier,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0f)
                        scaleX = widthPercent
                    }
                    .drawBehind {
                        drawRect(animatedIndicatorColor)
                    },
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            minimumCharacterCount?.let { minCount ->
                MinimumCharacterCount(
                    minimumRequirementMet = currentCharacterCount >= minCount,
                    minimumCharacterCount = minCount,
                )
            }
            Text(
                text = strengthLabel,
                style = MaterialTheme.typography.labelSmall,
                color = indicatorColor,
            )
        }
    }
}

@Composable
private fun MinimumCharacterCount(
    modifier: Modifier = Modifier,
    minimumRequirementMet: Boolean,
    minimumCharacterCount: Int,
) {
    val characterCountColor by animateColorAsState(
        targetValue = if (minimumRequirementMet) {
            strongColor
        } else {
            MaterialTheme.colorScheme.surfaceDim
        },
        label = "minimumCharacterCountColor",
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = if (minimumRequirementMet) {
                Icons.Default.CheckCircle
            } else {
                Icons.Default.Close
            },
            label = "iconForMinimumCharacterCount",
        ) {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = characterCountColor,
                modifier = Modifier.size(12.dp),
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$minimumCharacterCount characters",
            color = characterCountColor,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

enum class PasswordStrengthState {
    NONE,
    WEAK_1,
    WEAK_2,
    WEAK_3,
    GOOD,
    STRONG,
    VERY_STRONG,
}

private val strongColor = Color(0xFF41B06D)
private val weakColor = Color(0xFF8B6609)

@Preview
@Composable
private fun PasswordStrengthIndicatorPreview_minCharMet() {
    MifosTheme {
        PasswordStrengthIndicator(
            state = PasswordStrengthState.WEAK_3,
            currentCharacterCount = 12,
            minimumCharacterCount = 12,
        )
    }
}

@Preview
@Composable
private fun PasswordStrengthIndicatorPreview_minCharNotMet() {
    MifosTheme {
        PasswordStrengthIndicator(
            state = PasswordStrengthState.WEAK_3,
            currentCharacterCount = 11,
            minimumCharacterCount = 12,
        )
    }
}

@Preview
@Composable
private fun PasswordStrengthIndicatorPreview_noMinChar() {
    MifosTheme {
        PasswordStrengthIndicator(
            state = PasswordStrengthState.WEAK_3,
            currentCharacterCount = 12,
        )
    }
}
