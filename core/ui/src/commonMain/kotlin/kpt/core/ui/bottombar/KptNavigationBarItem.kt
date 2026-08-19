/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.ui.bottombar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kpt.core.base.designsystem.theme.KptTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun RowScope.KptNavigationBarItem(
    contentDescriptionRes: StringResource,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = stringResource(contentDescriptionRes),
                tint = Color.Unspecified,
            )
        },
        label = {
            Spacer(
                modifier = Modifier
                    .height(4.dp)
                    .width(10.dp)
                    .background(
                        color = KptTheme.colorScheme.primary,
                        shape = RoundedCornerShape(7.dp),
                    )
                    .animateContentSize(),
            )
        },
        selected = false,
        alwaysShowLabel = isSelected,
        onClick = onClick,
        modifier = modifier,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = KptTheme.colorScheme.primary,
            unselectedIconColor = KptTheme.colorScheme.primary,
            indicatorColor = Color.Transparent,
        ),
    )
}
