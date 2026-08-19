/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store.di

import kpt.core.database.AppDatabase
import kpt.core.store.AppStoreRegistry
import kpt.core.store.infra.StoreCacheManager
import kpt.core.store.infra.impl.StoreCacheManagerImpl
import kpt.core.store.wallet.account.provideAccountDetailStore
import kpt.core.store.wallet.autopay.provideAutoPayBillStore
import kpt.core.store.wallet.beneficiary.provideBeneficiaryStore
import kpt.core.store.wallet.biller.provideAutoPayBillerStore
import kpt.core.store.wallet.client.provideClientDetailStore
import kpt.core.store.wallet.history.provideHistoryStore
import kpt.core.store.wallet.invoice.provideInvoiceStore
import kpt.core.store.wallet.linkableaccount.provideLinkableAccountsStore
import kpt.core.store.wallet.notification.provideNotificationStore
import kpt.core.store.wallet.office.provideOfficeStore
import kpt.core.store.wallet.pocket.providePocketStore
import kpt.core.store.wallet.recentpayee.provideRecentPayeeStore
import kpt.core.store.wallet.savedcards.provideSavedCardStore
import kpt.core.store.wallet.selfaccounts.provideSelfAccountsStore
import kpt.core.store.wallet.standinginstruction.provideStandingInstructionStore
import kpt.core.store.wallet.transferdetail.provideTransferDetailStore
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for app-level Store wiring.
 *
 * Bindings emitted at Phase-4 Batch A + Phase-5 Batch-1 + Phase-5 Batch-2:
 *
 * - [StoreCacheManager] — registration-based logout drain (bookkeeper + drafts + registered stores).
 * - [AppStoreRegistry.History] — LEDGER read store (GOAL D13). Injects the fork's
 *   `SelfServiceApiManager` + `TransactionDao`; the store's writer performs
 *   atomic `replacePage(accountId, entities)`.
 * - [AppStoreRegistry.AutoPayBills] — OFFLINE_LOCAL_ONLY store (GOAL D12).
 *   Injects `BillDao` only (no fetcher).
 * - [AppStoreRegistry.AccountDetail] — SINGLE-ROW-PER-KEY read store (GOAL D13).
 *   Injects `SelfServiceApiManager` + `SavingAccountDetailDao`; writer performs
 *   single-row `upsert(entity)` (Room's implicit transaction is atomic for one row).
 * - [AppStoreRegistry.Beneficiary] — LEDGER read store (GOAL D13). Injects
 *   `SelfServiceApiManager` + `BeneficiaryDao`; writer performs atomic
 *   `replacePage(clientId, entities)`.
 * - [AppStoreRegistry.SavedCards] — LEDGER read store (GOAL D13). Injects
 *   `FineractApiManager` (datatable endpoint lives on the Fineract mount) +
 *   `SavedCardDao`; writer performs atomic `replacePage(clientId, entities)`.
 * - [AppStoreRegistry.Invoice] — LEDGER read store (GOAL D13). Injects
 *   `FineractApiManager` (datatable endpoint lives on the Fineract mount) +
 *   `InvoiceDao`; writer performs atomic `replacePage(clientId, entities)`.
 * - [AppStoreRegistry.Notification] — LEDGER read store (GOAL D13). Injects
 *   `FineractApiManager` (notifications endpoint lives on the Fineract mount) +
 *   `NotificationDao`; writer performs atomic `replacePage(clientId, entities)`.
 * - [AppStoreRegistry.ClientDetail] — SINGLE-ROW-PER-KEY read store (GOAL D13).
 *   Injects `SelfServiceApiManager` + `ClientDetailDao`; writer performs
 *   single-row `upsert(entity)` (Room's implicit transaction is atomic for one row).
 * - [AppStoreRegistry.Pocket] — LEDGER read store (GOAL D13). Injects
 *   `SelfServiceApiManager` (compound fetch: pocketApi + clientsApi + shareAccountApi) +
 *   `PocketDao`; writer performs atomic `replacePage(clientId, entities)`.
 *   REPLACES the in-memory `detailedPocketCache` MutableStateFlow that
 *   Phase-4-deferred out of `PocketRepositoryImp`.
 * - [AppStoreRegistry.StandingInstruction] — LEDGER read store (GOAL D13).
 *   Injects `FineractApiManager` (standing-instruction endpoint lives on the
 *   Fineract mount) + `StandingInstructionDao`; writer performs atomic
 *   `replacePage(clientId, entities)`.
 * - [AppStoreRegistry.Offices] — LEDGER read store, global reference data
 *   (GOAL D13). Injects `SelfServiceApiManager` + `OfficeDao`; writer performs
 *   atomic full-table `replaceAll(entities)`. Keys on the value-less
 *   `OfficesKey` `data object` — one cache slot for the whole app (see
 *   `OfficesKey` KDoc for the singleton-key rationale).
 * - [AppStoreRegistry.SelfAccounts] — LEDGER read store (GOAL D13). Injects
 *   `SelfServiceApiManager` (mirrors `AccountRepositoryImpl.getSelfAccounts`,
 *   which routes through the self-service `clientsApi`) + `SelfAccountDao`;
 *   writer performs atomic `replacePage(clientId, entities)`.
 * - [AppStoreRegistry.AutoPayBillers] — OFFLINE_LOCAL_ONLY store (GOAL D12).
 *   Injects `BillerDao` only (no fetcher). Mirrors the pilot
 *   [AppStoreRegistry.AutoPayBills] shape for the parallel biller catalog.
 * - [AppStoreRegistry.RecentPayee] — LOCAL-DERIVED store (GOAL D12/D13
 *   hybrid). Injects `RecentPayeeDao` only (no fetcher). The derive walk
 *   still lives at the `RecentPayeeRepositoryImpl` layer and writes into the
 *   store via `notifyingWrite` + `RecentPayeeDao.replacePage(sourceAccountId, rows)`.
 * - [AppStoreRegistry.LinkableAccounts] — LEDGER read store (GOAL D13),
 *   manage-pocket linkable-accounts Store5 migration. Injects
 *   `SelfServiceApiManager` (compound fetch: clientsApi + N shareAccountApi
 *   round-trips) + `LinkableAccountDao` + `PocketDao` (snapshot the
 *   already-linked accountIds at fetch time — Room read only, no fetcher-side
 *   write to `wallet_pockets`). Writer performs atomic
 *   `replacePage(clientId, entities)`. REPLACES upstream PR #2057's
 *   multiplatform-settings `linkable_accounts` cache in
 *   `PocketPreferencesDataSource`.
 *
 * All stores register with [StoreCacheManagerImpl] at start-up so a logout
 * cascades `Store.clear()` → SoT `deleteAll` → DAO `deleteAll`.
 *
 * Wire into the Koin start-up:
 * ```kotlin
 * startKoin {
 *     modules(appStoreModule, /* ...other modules */)
 * }
 * ```
 */
val appStoreModule: Module = module {
    // Store cache manager — clears all registered caches on logout (registration-based).
    single<StoreCacheManager> {
        StoreCacheManagerImpl(
            bookkeeperDao = get(),
            draftDao = get(),
        )
    }

    // Provide DAO singletons the stores below inject. `AppDatabase` itself is bound
    // by `kpt.core.database.di.DatabaseModule`.
    single { get<AppDatabase>().transactionDao }
    single { get<AppDatabase>().billDao }
    // Phase-5 Batch-1 DAOs
    single { get<AppDatabase>().savingAccountDetailDao }
    single { get<AppDatabase>().beneficiaryDao }
    single { get<AppDatabase>().savedCardDao }
    // Phase-5 Batch-2 DAOs
    single { get<AppDatabase>().invoiceDao }
    single { get<AppDatabase>().notificationDao }
    single { get<AppDatabase>().clientDetailDao }
    single { get<AppDatabase>().pocketDao }
    // Phase-5 Batch-3 DAOs
    single { get<AppDatabase>().standingInstructionDao }
    single { get<AppDatabase>().officeDao }
    single { get<AppDatabase>().selfAccountDao }
    // Phase-5 Batch-4 DAOs
    single { get<AppDatabase>().billerDao }
    single { get<AppDatabase>().recentPayeeDao }
    // manage-pocket linkable-accounts Store5 migration DAO
    single { get<AppDatabase>().linkableAccountDao }
    // transfer-detail Store5 vertical DAO
    single { get<AppDatabase>().transferDetailDao }

    // Phase-4 pilots
    single(AppStoreRegistry.History) {
        provideHistoryStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.AutoPayBills) {
        provideAutoPayBillStore(dao = get())
    }

    // Phase-5 Batch-1 read-cache stores
    single(AppStoreRegistry.AccountDetail) {
        provideAccountDetailStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.Beneficiary) {
        provideBeneficiaryStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.SavedCards) {
        // Uses FineractApiManager (not SelfServiceApiManager) — parity with
        // SavedCardRepositoryImpl. Koin resolves by type since both apiManagers
        // are distinct concrete types.
        provideSavedCardStore(apiManager = get(), dao = get())
    }

    // Phase-5 Batch-2 read-cache stores
    single(AppStoreRegistry.Invoice) {
        // Uses FineractApiManager (invoice datatable lives on the Fineract mount).
        provideInvoiceStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.Notification) {
        // Uses FineractApiManager (notifications endpoint lives on the Fineract mount).
        provideNotificationStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.ClientDetail) {
        // Uses SelfServiceApiManager (self-service client endpoint).
        provideClientDetailStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.Pocket) {
        // Uses SelfServiceApiManager (pocket + clients + shareAccount all mount here).
        providePocketStore(apiManager = get(), dao = get())
    }

    // Phase-5 Batch-3 read-cache stores
    single(AppStoreRegistry.StandingInstruction) {
        // Uses FineractApiManager (standing-instruction endpoint lives on the Fineract mount).
        provideStandingInstructionStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.Offices) {
        // Uses SelfServiceApiManager — parity with OfficeRepositoryImpl (the
        // pre-store OfficeRepository binds through SelfServiceApiManager.officeApi).
        provideOfficeStore(apiManager = get(), dao = get())
    }
    single(AppStoreRegistry.SelfAccounts) {
        // Uses SelfServiceApiManager — parity with AccountRepositoryImpl.getSelfAccounts
        // which routes through the self-service clientsApi mount.
        provideSelfAccountsStore(apiManager = get(), dao = get())
    }

    // Phase-5 Batch-4 offline / local-derived stores
    single(AppStoreRegistry.AutoPayBillers) {
        // OFFLINE_LOCAL_ONLY — no fetcher (mirrors the AutoPayBills pilot).
        provideAutoPayBillerStore(dao = get())
    }
    single(AppStoreRegistry.RecentPayee) {
        // LOCAL-DERIVED — no fetcher. The derive walk lives at the repository
        // layer (`RecentPayeeRepositoryImpl`) and writes into this store via
        // `notifyingWrite("wallet_recent_payees") { … }`.
        provideRecentPayeeStore(dao = get())
    }

    // manage-pocket linkable-accounts Store5 migration (replaces upstream PR
    // #2057's multiplatform-settings `linkable_accounts` cache in
    // `PocketPreferencesDataSource`).
    single(AppStoreRegistry.LinkableAccounts) {
        // Uses SelfServiceApiManager — the source `clientsApi.getClientAccounts`
        // + per-SHARE-account `shareAccountApi.getShareAccountDetails` both
        // route through the self-service mount (parity with PocketStore). The
        // PocketDao is injected to snapshot the already-linked set at fetch
        // time (Room read only; no fetcher-side write to `wallet_pockets`).
        provideLinkableAccountsStore(
            apiManager = get(),
            dao = get(),
            pocketDao = get(),
        )
    }

    // transfer-detail Store5 vertical — SINGLE-ROW-PER-KEY read store (GOAL D13).
    single(AppStoreRegistry.TransferDetail) {
        // Uses SelfServiceApiManager — parity with AccountRepositoryImpl.getAccountTransfer
        // which routes through the self-service accountTransfersApi mount.
        provideTransferDetailStore(selfManager = get(), dao = get())
    }

    // Register stores with StoreCacheManager for logout clearing (GOAL D7).
    single(createdAtStart = true) {
        val mgr = get<StoreCacheManager>() as StoreCacheManagerImpl
        // Phase-4 pilots
        mgr.register(get(AppStoreRegistry.History))
        mgr.register(get(AppStoreRegistry.AutoPayBills))
        // Phase-5 Batch-1 read-cache stores
        mgr.register(get(AppStoreRegistry.AccountDetail))
        mgr.register(get(AppStoreRegistry.Beneficiary))
        mgr.register(get(AppStoreRegistry.SavedCards))
        // Phase-5 Batch-2 read-cache stores
        mgr.register(get(AppStoreRegistry.Invoice))
        mgr.register(get(AppStoreRegistry.Notification))
        mgr.register(get(AppStoreRegistry.ClientDetail))
        mgr.register(get(AppStoreRegistry.Pocket))
        // Phase-5 Batch-3 read-cache stores
        mgr.register(get(AppStoreRegistry.StandingInstruction))
        mgr.register(get(AppStoreRegistry.Offices))
        mgr.register(get(AppStoreRegistry.SelfAccounts))
        // Phase-5 Batch-4 offline / local-derived stores
        mgr.register(get(AppStoreRegistry.AutoPayBillers))
        mgr.register(get(AppStoreRegistry.RecentPayee))
        // manage-pocket linkable-accounts Store5 migration
        mgr.register(get(AppStoreRegistry.LinkableAccounts))
        // transfer-detail Store5 vertical
        mgr.register(get(AppStoreRegistry.TransferDetail))
    }
}
