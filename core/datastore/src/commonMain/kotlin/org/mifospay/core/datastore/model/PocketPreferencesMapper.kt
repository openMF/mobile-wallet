/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore.model

import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount

fun PocketAccount.toEntity() = PocketAccountEntity(
    pocketId = pocketId,
    id = id,
    accountId = accountId,
    accountTypeStr = accountType.name,
    accountNumber = accountNumber,
)

fun PocketAccountEntity.toDomain() = PocketAccount(
    pocketId = pocketId,
    id = id,
    accountId = accountId,
    accountType = runCatching { AccountType.valueOf(accountTypeStr) }.getOrDefault(AccountType.SAVINGS),
    accountNumber = accountNumber,
)

fun DetailedPocketAccount.toEntity() = DetailedPocketAccountEntity(
    pocket = pocket.toEntity(),
    productName = productName,
    balance = balance,
    currencyCode = currencyCode,
    currencyDisplaySymbol = currencyDisplaySymbol,
    decimalPlaces = decimalPlaces,
    statusStr = status?.name,
)

fun DetailedPocketAccountEntity.toDomain() = DetailedPocketAccount(
    pocket = pocket.toDomain(),
    productName = productName,
    balance = balance,
    currencyCode = currencyCode,
    currencyDisplaySymbol = currencyDisplaySymbol,
    decimalPlaces = decimalPlaces,
    status = statusStr?.let { runCatching { AccountStatus.valueOf(it) }.getOrNull() },
)

fun LinkableAccount.toEntity() = LinkableAccountEntity(
    accountId = accountId,
    productName = productName,
    accountNumber = accountNumber,
    accountTypeStr = accountType.name,
    balance = balance,
    currencyCode = currencyCode,
    currencyDisplaySymbol = currencyDisplaySymbol,
    decimalPlaces = decimalPlaces,
    statusStr = status?.name,
)

fun LinkableAccountEntity.toDomain() = LinkableAccount(
    accountId = accountId,
    productName = productName,
    accountNumber = accountNumber,
    accountType = runCatching { AccountType.valueOf(accountTypeStr) }.getOrDefault(AccountType.SAVINGS),
    balance = balance,
    currencyCode = currencyCode,
    currencyDisplaySymbol = currencyDisplaySymbol,
    decimalPlaces = decimalPlaces,
    status = statusStr?.let { runCatching { AccountStatus.valueOf(it) }.getOrNull() },
)
