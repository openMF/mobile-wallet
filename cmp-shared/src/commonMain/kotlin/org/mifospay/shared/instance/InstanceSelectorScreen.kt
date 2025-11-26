/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.instance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.cmp_shared.generated.resources.Res
import mobile_wallet.cmp_shared.generated.resources.default
import mobile_wallet.cmp_shared.generated.resources.error_message
import mobile_wallet.cmp_shared.generated.resources.interbank_server
import mobile_wallet.cmp_shared.generated.resources.main_server
import mobile_wallet.cmp_shared.generated.resources.select_instance
import mobile_wallet.cmp_shared.generated.resources.tenant_value
import mobile_wallet.cmp_shared.generated.resources.update
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.model.instance.InstanceType
import org.mifospay.core.model.instance.ServerInstance
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.KptTheme
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceSelectorScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InstanceSelectorViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    // Handle events
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is InstanceSelectorEvent.DismissSheet -> onDismiss()
        }
    }

    MifosBottomSheet(
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        MifosScaffold(
            modifier = modifier,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(KptTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                ) {
                    MifosButton(
                        onClick = {
                            viewModel.trySendAction(InstanceSelectorAction.UpdateInstances)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.isUpdateEnabled,
                    ) {
                        Text(stringResource(Res.string.update))
                    }
                }
            },
        ) { contentPadding ->
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        MifosProgressIndicator()
                    }
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.error_message, state.error ?: ""),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                else -> {
                    InstancesList(
                        modifier = Modifier.padding(contentPadding),
                        mainInstances = state.mainInstances,
                        interbankInstances = state.interbankInstances,
                        selectedMainInstance = state.tempSelectedMainInstance,
                        selectedInterbankInstance = state.tempSelectedInterbankInstance,
                        onInstanceSelected = { instance ->
                            viewModel.trySendAction(InstanceSelectorAction.SelectInstance(instance))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun InstancesList(
    mainInstances: List<ServerInstance>,
    interbankInstances: List<ServerInstance>,
    selectedMainInstance: ServerInstance?,
    selectedInterbankInstance: ServerInstance?,
    onInstanceSelected: (ServerInstance) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            // Header with drag handle
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.select_instance),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }

        if (mainInstances.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.main_server),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            items(mainInstances) { instance ->
                InstanceItem(
                    instance = instance,
                    isSelected = instance == selectedMainInstance,
                    onClick = { onInstanceSelected(instance) },
                )
            }
        }

        if (interbankInstances.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.interbank_server),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            items(interbankInstances) { instance ->
                InstanceItem(
                    instance = instance,
                    isSelected = instance == selectedInterbankInstance,
                    onClick = { onInstanceSelected(instance) },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InstanceItem(
    instance: ServerInstance,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = KptTheme.colorScheme.surface,
        ),
        shape = KptTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                modifier = Modifier.padding(0.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Instance Label
                Text(
                    text = instance.label,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Instance Type Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = instance.type.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )

                    if (instance.isDefault) {
                        Text(
                            text = stringResource(Res.string.default),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }

                // Endpoint URL
                Text(
                    text = instance.endpoint,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Tenant ID
                Text(
                    text = stringResource(Res.string.tenant_value, instance.platformTenantId),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun InstanceItemPreview() {
    KptTheme {
        InstanceItem(
            instance = ServerInstance(
                endpoint = "mifos-bank-2.mifos.community",
                protocol = "https://",
                path = "/fineract-provider/api/v1/",
                platformTenantId = "mifos-bank-2",
                label = "Production",
                type = InstanceType.MAIN,
                isDefault = true,
            ),
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun InstanceItemUnselectedPreview() {
    MifosTheme {
        InstanceItem(
            instance = ServerInstance(
                endpoint = "apis.flexcore.mx",
                protocol = "https://",
                path = "/v1.0/vnext2/",
                platformTenantId = "interbank-1",
                label = "Interbank",
                type = InstanceType.INTERBANK,
                isDefault = false,
            ),
            isSelected = false,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun InstancesListPreview() {
    MifosTheme {
        InstancesList(
            mainInstances = listOf(
                ServerInstance(
                    endpoint = "mifos-bank-2.mifos.community",
                    protocol = "https://",
                    path = "/fineract-provider/api/v1/",
                    platformTenantId = "mifos-bank-2",
                    label = "Production",
                    type = InstanceType.MAIN,
                    isDefault = true,
                ),
                ServerInstance(
                    endpoint = "mifos-bank-1.mifos.community",
                    protocol = "https://",
                    path = "/fineract-provider/api/v1/",
                    platformTenantId = "mifos-bank-1",
                    label = "Staging",
                    type = InstanceType.MAIN,
                    isDefault = false,
                ),
            ),
            interbankInstances = listOf(
                ServerInstance(
                    endpoint = "apis.flexcore.mx",
                    protocol = "https://",
                    path = "/v1.0/vnext2/",
                    platformTenantId = "interbank-1",
                    label = "Interbank Production",
                    type = InstanceType.INTERBANK,
                    isDefault = true,
                ),
            ),
            selectedMainInstance = ServerInstance(
                endpoint = "mifos-bank-2.mifos.community",
                protocol = "https://",
                path = "/fineract-provider/api/v1/",
                platformTenantId = "mifos-bank-2",
                label = "Production",
                type = InstanceType.MAIN,
                isDefault = true,
            ),
            selectedInterbankInstance = null,
            onInstanceSelected = {},
        )
    }
}
