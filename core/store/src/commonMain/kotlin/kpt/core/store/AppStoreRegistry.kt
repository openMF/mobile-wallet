/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store

import kpt.core.base.store.infra.StoreRegistry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Application-level [StoreRegistry] — the single named-qualifier registry for every
 * `org.mobilenativefoundation.store.store5.Store` the app exposes.
 *
 * Pilot stores (Phase-4 Batch A):
 *
 * - [History] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic replacePage). GOAL D13.
 * - [AutoPayBills] — OFFLINE_LOCAL_ONLY (createOfflineStore + notifyingWrite). GOAL D12.
 *
 * Phase-5 Batch-1 read-cache stores:
 *
 * - [AccountDetail] — SINGLE-ROW-PER-KEY read (createStore + CACHE_FIRST_SWR + upsert).
 *   Keys on `AccountDetailKey(accountId)`; consumed by
 *   `SavingsAccountRepositoryImpl.getAccountDetailScreen(...)`. GOAL D13.
 * - [Beneficiary] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic replacePage).
 *   Keys on `BeneficiaryKey(clientId)`; consumed by
 *   `SelfServiceRepositoryImpl.getBeneficiaryListScreen(...)`. GOAL D13.
 * - [SavedCards] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic replacePage).
 *   Keys on `SavedCardKey(clientId)`; consumed by
 *   `SavedCardRepositoryImpl.getSavedCardsScreen(...)`. GOAL D13.
 *
 * Phase-5 Batch-2 read-cache stores (this batch):
 *
 * - [Invoice] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic replacePage).
 *   Keys on `InvoiceKey(clientId)`; consumed by
 *   `InvoiceRepositoryImpl.getInvoicesScreen(...)`. GOAL D13.
 * - [Notification] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic replacePage).
 *   Keys on `NotificationKey(clientId)`; consumed by
 *   `NotificationRepositoryImpl.fetchNotificationsScreen(...)`. GOAL D13.
 * - [ClientDetail] — SINGLE-ROW-PER-KEY read (createStore + CACHE_FIRST_SWR + upsert).
 *   Keys on `ClientDetailKey(clientId)`; consumed by
 *   `ClientRepositoryImpl.getClientInfoScreen(...)`. GOAL D13.
 * - [Pocket] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic replacePage).
 *   Keys on `PocketKey(clientId)`; consumed by
 *   `PocketRepositoryImp.getDetailedPocketAccountsScreen(...)`. REPLACES the
 *   in-memory `detailedPocketCache` MutableStateFlow (Phase-4-deferred, now
 *   Room-backed). GOAL D13.
 *
 * Phase-5 Batch-3 read-cache stores (this batch):
 *
 * - [StandingInstruction] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic
 *   replacePage). Keys on `StandingInstructionKey(clientId)`; consumed by
 *   `StandingInstructionRepositoryImpl.getAllStandingInstructionsScreen(...)`. GOAL D13.
 * - [Offices] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic full-table
 *   replaceAll). Keys on the value-less `OfficesKey` `data object` (see its
 *   KDoc — global reference data, one cache slot for the whole app); consumed
 *   by `OfficeRepositoryImpl.getOfficesScreen(...)`. GOAL D13.
 * - [SelfAccounts] — LEDGER read (createStore + CACHE_FIRST_SWR + atomic
 *   replacePage). Keys on `SelfAccountsKey(clientId)`; consumed by
 *   `AccountRepositoryImpl.getSelfAccountsScreen(...)`. GOAL D13.
 *
 * Batch-3 also delivers the `receipt` feature WITHOUT its own store (per GOAL
 * D13 — reads off the existing `wallet_transactions` LEDGER; see
 * `ReceiptViewModel` KDoc). No new qualifier / TTL / registration is emitted
 * for `receipt` here.
 *
 * Phase-5 Batch-4 offline / local-derived stores (this batch):
 *
 * - [AutoPayBillers] — OFFLINE_LOCAL_ONLY (createOfflineStore + notifyingWrite).
 *   Key = [Unit]; mirrors the [AutoPayBills] pilot's shape for the parallel
 *   biller catalog. Consumed by `BillerOfflineRepositoryImpl` (bound to the
 *   grandfathered `org.mifospay.core.datastore.BillerRepository` interface).
 *   No TTL — offline-local-only never fetches. GOAL D12.
 * - [RecentPayee] — LOCAL-DERIVED persist-on-derive
 *   (createOfflineStore + notifyingWrite). Keys on
 *   `RecentPayeeKey(sourceAccountId)`; the derive walk (N+1 fan-out over
 *   savings-with-associations + per-transfer detail) still lives in
 *   `RecentPayeeRepositoryImpl` and writes into the store via
 *   `notifyingWrite("wallet_recent_payees") { dao.replacePage(sourceAccountId, rows) }`.
 *   Reads are pure Room, so cache hits skip the N+1 entirely. No TTL — the
 *   cache is refreshed on every screen entry via a background derive kick.
 *   GOAL D12/D13 hybrid.
 *
 * Batch-4 also delivers:
 *  - `getAccountAndBeneficiaryListScreen` — a new store-native combinator in
 *    `SelfServiceRepository` that folds the network account-list stream with
 *    the store-backed beneficiary stream via a manual `combine {}` fold over
 *    `org.mifospay.core.common.ScreenState` (fork's ScreenState, not the
 *    template's — the `combineScreenStates` helper in `core-base/store` is
 *    typed against `kpt.core.base.store.screen.ScreenState` and cannot be
 *    reused verbatim). No new qualifier / TTL / registration here.
 *  - **STUB verdict for `merchants`** — the wallet app has no server merchant
 *    endpoint (neither `SelfServiceApiManager` nor `FineractApiManager`
 *    exposes a `merchantApi`; the pre-existing `MerchantViewModel` returns
 *    hardcoded `emptyList()` and takes no repository dependency). No
 *    [merchants] qualifier is emitted; a `TODO` in `MerchantViewModel` records
 *    the verdict for the day the endpoint materializes.
 *
 * Each store's per-qualifier TTL is exposed via [Ttl] + the runtime-lookup
 * [ttlByName] map — kept aligned with the template's dual compile-time /
 * runtime pattern so the read-side `asScreenStream(ttl = …)` call can either
 * pull the constant directly OR look up by qualifier-name string.
 */
object AppStoreRegistry : StoreRegistry() {

    // Phase-4 pilots
    val History = store("history")
    val AutoPayBills = store("autoPayBills")

    // Phase-5 Batch-1 read-cache stores
    val AccountDetail = store("accountDetail")
    val Beneficiary = store("beneficiary")
    val SavedCards = store("savedCards")

    // Phase-5 Batch-2 read-cache stores
    val Invoice = store("invoice")
    val Notification = store("notification")
    val ClientDetail = store("clientDetail")
    val Pocket = store("pocket")

    // Phase-5 Batch-3 read-cache stores (this batch)
    val StandingInstruction = store("standingInstruction")
    val Offices = store("offices")
    val SelfAccounts = store("selfAccounts")

    // Phase-5 Batch-4 offline / local-derived stores (this batch)
    val AutoPayBillers = store("autoPayBillers")
    val RecentPayee = store("recentPayee")

    /**
     * manage-pocket linkable-accounts LEDGER read (`createStore` +
     * CACHE_FIRST_SWR + atomic replacePage). Keys on
     * `LinkableAccountKey(clientId)`; consumed by
     * `PocketRepositoryImp.getAvailableAccountsToLinkScreen(...)`.
     *
     * REPLACES upstream PR #2057's multiplatform-settings `linkable_accounts`
     * cache in `PocketPreferencesDataSource` — this branch's Store5
     * architecture serves the same read offline-first through Room SoT
     * (mirrors `PocketStore` / `BeneficiaryStore` / `SelfAccountsStore`
     * recipe). GOAL D13.
     */
    val LinkableAccounts = store("linkableAccounts")

    /**
     * transfer-detail SINGLE-ROW-PER-KEY read (`createStore` + CACHE_FIRST_SWR +
     * single-row upsert). Keys on `TransferDetailKey(transferId)`; consumed by
     * `AccountRepositoryImpl.getAccountTransferStream(...)`. Makes the
     * transaction-detail drill-down offline-first — mirrors the [AccountDetail]
     * recipe for the `getAccountTransfer` endpoint. GOAL D13.
     */
    val TransferDetail = store("transferDetail")

    /** TTL durations — financial-read freshness windows. */
    object Ttl {
        /**
         * `history` LEDGER TTL — 2 minutes. Financial-read cadence: transactions
         * settle in seconds server-side, so a 2-minute band feels responsive
         * without hammering the API on rapid tab-switches. Consumed by
         * `store.asScreenStream(ttl = History TTL)` at the CACHE_FIRST_SWR band gate.
         */
        val HISTORY: Duration = 2.minutes

        /**
         * `accountDetail` SINGLE-ROW-PER-KEY TTL — 2 minutes. Same cadence as
         * [HISTORY] because the detail record derives from the same
         * `getSavingsWithAssociations` endpoint; keeping the two TTLs in lockstep
         * means a user pulling account-detail and history in the same session
         * sees a single coherent freshness band.
         */
        val ACCOUNT_DETAIL: Duration = 2.minutes

        /**
         * `beneficiary` LEDGER TTL — 15 minutes. Beneficiary lists change on
         * explicit user CRUD, not automatic server events, so a longer window
         * suppresses redundant refresh traffic on rapid transfer-flow navigation.
         * User-triggered writes (create/update/delete) still trigger a manual
         * refresh through the VM's `RefreshList` action.
         */
        val BENEFICIARY: Duration = 15.minutes

        /**
         * `savedCards` LEDGER TTL — 15 minutes. Same reasoning as [BENEFICIARY] —
         * saved-card lists change on explicit user CRUD, not server-side events,
         * so a longer band is appropriate.
         */
        val SAVED_CARDS: Duration = 15.minutes

        /**
         * `invoice` LEDGER TTL — 5 minutes. Invoices sit between transaction
         * cadence (fast — HISTORY 2m) and CRUD-only lists (slow — BENEFICIARY 15m).
         * A merchant may create an invoice at any point on the backend; a
         * moderate refresh cadence keeps the client list current without
         * hammering the datatable endpoint.
         */
        val INVOICE: Duration = 5.minutes

        /**
         * `notification` LEDGER TTL — 5 minutes. Notifications trickle in from
         * server events at unpredictable cadences; a moderate revalidation band
         * balances read freshness against endpoint load on a fast-tap
         * notification-bell open.
         */
        val NOTIFICATION: Duration = 5.minutes

        /**
         * `clientDetail` SINGLE-ROW-PER-KEY TTL — 15 minutes. Client profile
         * (`Client` payload — name/email/mobile/office/status/timeline) rarely
         * changes; a long band keeps the profile screen open-instantly and only
         * revalidates on explicit refresh or the 15-minute stale edge.
         */
        val CLIENT_DETAIL: Duration = 15.minutes

        /**
         * `pocket` LEDGER TTL — 5 minutes. Pocket balances shift on any
         * downstream loan/savings/share balance change (deposit, withdrawal,
         * market-price recalculation for shares). A 5-minute band matches
         * account-balance freshness expectations while cushioning the N+2
         * compound fetch (basic + client-accounts + N share market-price
         * round-trips) the fetcher incurs.
         */
        val POCKET: Duration = 5.minutes

        /**
         * `standingInstruction` LEDGER TTL — 10 minutes. Standing instructions
         * change on explicit user CRUD (create/update/delete) rather than
         * automatic server events, so a longer band than HISTORY is fine;
         * shorter than BENEFICIARY because instructions have a scheduled
         * transfer side-effect and a stale-list impression can be materially
         * misleading (a deleted instruction still showing while a scheduled
         * transfer no longer fires).
         */
        val STANDING_INSTRUCTION: Duration = 10.minutes

        /**
         * `offices` LEDGER TTL — 60 minutes. Fineract office data is
         * effectively static organizational reference data (the office tree
         * evolves at admin-review cadence, not user-session cadence). A long
         * band is appropriate; the FastMpayProcessor's lookup pattern is
         * `find { it.id == qrData.officeId }`, and the alternative to a stale
         * cache hit is the DEFAULT_OFFICE_NAME fallback — either way the QR
         * flow keeps moving.
         */
        val OFFICES: Duration = 60.minutes

        /**
         * `selfAccounts` SINGLE-ROW-PER-KEY-family LEDGER TTL — 2 minutes.
         * Same cadence as [HISTORY] because a user's own accounts share the
         * same balance-freshness expectations as their transaction feed; a
         * short revalidation window keeps the QR-generation picker showing
         * up-to-date balances after any transaction posts.
         */
        val SELF_ACCOUNTS: Duration = 2.minutes

        /**
         * `linkableAccounts` LEDGER TTL — 2 minutes. Same cadence as
         * [SELF_ACCOUNTS] because the linkable-accounts derivation source is
         * the SAME `clientsApi.getClientAccounts` endpoint (plus a per-SHARE
         * market-price round-trip and a snapshot-filter against the
         * `wallet_pockets` LEDGER). Keeping the two TTLs in lockstep means a
         * user opening the link-accounts sheet immediately after a balance
         * change on the dashboard sees matching balance figures.
         */
        val LINKABLE_ACCOUNTS: Duration = 2.minutes

        /**
         * `transferDetail` SINGLE-ROW-PER-KEY TTL — 2 minutes. Same cadence as
         * [ACCOUNT_DETAIL] because the transfer-detail record derives from the
         * same financial-read family as the account detail it drills into;
         * keeping the two TTLs in lockstep means a user opening a transaction
         * detail immediately after viewing the account sees a coherent freshness
         * band.
         */
        val TRANSFER_DETAIL: Duration = 2.minutes
    }

    /**
     * Runtime lookup of per-store TTL by qualifier name — paired with the
     * compile-time [Ttl] object so `ScreenDataStream` / `asScreenStream` can
     * resolve a store's TTL dynamically when the qualifier is only known at
     * runtime.
     *
     * `autoPayBills`, `autoPayBillers`, and `recentPayee` intentionally have
     * NO TTL entries — they are OFFLINE_LOCAL_ONLY / LOCAL-DERIVED and never
     * issue a network fetch; the freshness band would be permanently `Fresh`
     * (nothing to revalidate). Callers that need a background refresh on
     * `recentPayee` (the derive walk) kick it explicitly at the repository
     * layer, not through the freshness engine.
     */
    val ttlByName: Map<String, Duration> = mapOf(
        "history" to Ttl.HISTORY,
        "accountDetail" to Ttl.ACCOUNT_DETAIL,
        "beneficiary" to Ttl.BENEFICIARY,
        "savedCards" to Ttl.SAVED_CARDS,
        "invoice" to Ttl.INVOICE,
        "notification" to Ttl.NOTIFICATION,
        "clientDetail" to Ttl.CLIENT_DETAIL,
        "pocket" to Ttl.POCKET,
        "standingInstruction" to Ttl.STANDING_INSTRUCTION,
        "offices" to Ttl.OFFICES,
        "selfAccounts" to Ttl.SELF_ACCOUNTS,
        "linkableAccounts" to Ttl.LINKABLE_ACCOUNTS,
        "transferDetail" to Ttl.TRANSFER_DETAIL,
    )
}
