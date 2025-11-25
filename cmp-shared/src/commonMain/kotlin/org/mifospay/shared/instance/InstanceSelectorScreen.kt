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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.instance.ServerInstance
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceSelectorScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InstanceSelectorViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Instance") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = MifosIcons.ArrowBack,
                            contentDescription = "Close",
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        when (val state = uiState) {
            is InstanceSelectorUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading instances...")
                }
            }

            is InstanceSelectorUiState.Success -> {
                InstancesList(
                    instances = state.instances,
                    selectedInstance = state.selectedInstance,
                    onInstanceSelected = { instance ->
                        viewModel.selectInstance(instance)
                        onDismiss()
                    },
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is InstanceSelectorUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun InstancesList(
    instances: List<ServerInstance>,
    selectedInstance: ServerInstance?,
    onInstanceSelected: (ServerInstance) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(instances) { instance ->
            InstanceItem(
                instance = instance,
                isSelected = instance == selectedInstance,
                onClick = { onInstanceSelected(instance) },
            )
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = instance.label,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = instance.fullUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tenant: ${instance.platformTenantId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
            )
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
                isDefault = true,
            ),
            isSelected = true,
            onClick = {},
        )
    }
}
