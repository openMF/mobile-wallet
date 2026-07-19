# Loan Application Feature — Architecture & Provider Selection

## Overview

`libs/mifos-loans` is a self-contained Kotlin Multiplatform module implementing a 6-step
loan-application wizard, ported from mifos-mobile's `feature/loan-application` and re-architected
so the app can plug in different lender backends behind a single UI flow.

```
Select Loan Provider → Select Loan Type → Loan Product Details → Loan Apply → Upload Docs → Confirm Details
```

The module owns its full vertical slice: domain models, its own Ktorfit network client, mappers,
provider adapters, a repository facade, DI, and all 6 screens. The host app (`cmp-shared`) only
knows about one entry point (`navigateToLoanApplicationGraph`) and one Koin module
(`LoanApplicationModule`).

---

## Why a provider-selection step exists

The product requirement was: tapping "Apply for Loan" should first show a choice of lenders
(HDFC Bank, SBI, ICICI Bank, Axis Bank today — illustrative names, not live integrations), *then*
proceed into the application flow. The rest of this document explains how that choice is threaded
through the wizard and turned into an actual backend selection, even though today every provider
still resolves to the same mock data.

---

## Package layout

```
libs/mifos-loans/src/commonMain/kotlin/org/mifos/lib/loan/
├── core/                    LoanProvider interface + all domain models (LoanTemplate, LoansPayload, ...)
├── network/                 Ktorfit LoansService + DTOs + the library's OWN Ktorfit engine (di/LoansNetworkModule.kt)
├── mapper/                  DTO → domain model conversions
├── providers/                Concrete LoanProvider adapters + the registry that resolves between them
│   ├── FineractLoanProvider.kt    real Fineract-backed adapter (not active yet)
│   ├── DummyLoanProvider.kt       mock adapter (currently backs every provider id)
│   └── LoanProviderRegistry.kt    resolves a LoanProvider by providerId
├── repository/
│   ├── LoansRepository.kt         facade interface — this is what ViewModels inject
│   └── LoansRepositoryImpl.kt     delegates every call to LoanProviderRegistry.resolve(providerId)
├── di/LoanApplicationModule.kt    Koin bindings for all of the above + the 6 screen ViewModels
├── ui/                            the 6 screens of the wizard
│   ├── selectLoanProvider/            Screen 1
│   ├── selectLoanType/                Screen 2
│   ├── loanProductDetails/            Screen 3
│   ├── loanApply/                     Screen 4
│   ├── uploadDocs/                    Screen 5
│   └── confirmDetails/                Screen 6
├── navigation/LoanApplicationNavGraph.kt   wires all 6 screens together
└── component/                     shared Compose UI pieces (LoanCard, etc.)
```

---

## How `providerId` flows through the wizard

**The core design decision:** the selected provider is carried as a plain field on every screen's
navigation route (`providerId: String`) — the exact same mechanism already used for `clientId`.
It is **not** a shared mutable singleton/holder.

Why that matters: Android can kill the app process mid-flow (backgrounding, memory pressure). The
nav backstack and each screen's arguments survive this via `SavedStateHandle` — a singleton held
in a Koin container does not. Since this flow ends in a non-reversible submit, silently losing
"which provider you were applying through" after a process restart would be a real correctness bug.
Threading it through routes costs one extra field per screen and gets this for free.

Concretely, every route in the chain looks like this (fields accumulate as the wizard progresses):

```kotlin
SelectLoanProviderRoute(clientId)
SelectLoanTypeRoute(clientId, providerId)
LoanProductDetailsRoute(clientId, productId, providerId)
LoanApplyRoute(clientId, productId, providerId)
UploadDocsRoute(clientId, productId, providerId, applicantName, loanProductName, loanPurpose, disbursementDate, principalAmount)
ConfirmDetailsRoute(clientId, productId, providerId, applicantName, loanProductName, loanPurpose, disbursementDate, principalAmount)
```

Each screen's ViewModel reads `providerId` off its own route via `savedStateHandle.toRoute<XRoute>()`,
keeps it in its UI state, and passes it forward when it navigates to the next screen — mirroring
exactly how `clientId` and `productId` are already threaded.

---

## The resolution mechanism: registry + facade

### 1. `LoanProvider` (interface, in `core/`) — unchanged, the seam every adapter implements

```kotlin
interface LoanProvider {
    fun getLoanTemplate(clientId: Long): Flow<DataState<LoanTemplate>>
    fun getLoanTemplateByProduct(clientId: Long, productId: Long): Flow<DataState<LoanTemplate>>
    suspend fun submitLoanApplication(loanState: LoanState, payload: LoansPayload, loanId: Long): DataState<String>
}
```

### 2. `providers/` — concrete adapters

- **`DummyLoanProvider`** — no network calls, returns fabricated `LoanTemplate`/success data. Currently backs every provider id.
- **`FineractLoanProvider`** — the real adapter, calls the library's own `LoansService` (Ktorfit) against the live Fineract self-service API. Built and wired, but not reachable from the UI today (see "Current state" below).

### 3. `LoanProviderRegistry` — resolves an id to an adapter

```kotlin
class LoanProviderRegistry(
    private val providersById: Map<String, LoanProvider>,
    private val defaultProvider: LoanProvider,
) {
    fun resolve(providerId: String): LoanProvider = providersById[providerId] ?: defaultProvider
}
```

Bound in `LoanApplicationModule.kt` today as:

```kotlin
providersById = mapOf(
    "hdfc" to dummy,
    "sbi" to dummy,
    "icici" to dummy,
    "axis" to dummy,
    "fineract" to get<FineractLoanProvider>(),
),
defaultProvider = dummy,
```

All four UI-facing ids point at the **same** `DummyLoanProvider` instance — this is the "current
state" honesty: selecting HDFC vs SBI vs ICICI vs Axis produces identical data today. `"fineract"`
is registered and ready, just not selectable from the current 4-item provider list.

### 4. `LoansRepository` — the facade ViewModels actually inject

None of the 4 data-consuming ViewModels (`SelectLoanTypeViewModel`, `LoanProductDetailsViewModel`,
`LoanApplyViewModel`, `ConfirmDetailsViewModel`) inject `LoanProvider` directly anymore. They inject
`LoansRepository`, which takes `providerId` as a parameter on every call and resolves through the
registry:

```kotlin
interface LoansRepository {
    fun getLoanTemplate(providerId: String, clientId: Long): Flow<DataState<LoanTemplate>>
    fun getLoanTemplateByProduct(providerId: String, clientId: Long, productId: Long): Flow<DataState<LoanTemplate>>
    suspend fun submitLoanApplication(providerId: String, loanState: LoanState, payload: LoansPayload, loanId: Long): DataState<String>
}
```

`LoansRepositoryImpl` is a one-line delegation: `registry.resolve(providerId).getLoanTemplate(clientId)`, etc.

---

## Walkthrough: user taps "SBI"

1. **Select Loan Provider screen** — `SelectLoanProviderViewModel` holds a static list (`defaultLoanProviders`, ids `"hdfc"`/`"sbi"`/`"icici"`/`"axis"`). Tapping SBI dispatches `ProviderClicked("sbi")`, which now forwards `providerId = "sbi"` into the next navigation call (previously this was discarded — that was the bug fixed).
2. **Select Loan Type screen** — receives `SelectLoanTypeRoute(clientId, providerId = "sbi")`. Its ViewModel calls `loansRepository.getLoanTemplate(providerId = "sbi", clientId)`. `LoansRepositoryImpl` calls `registry.resolve("sbi")` → returns the shared `DummyLoanProvider` → mock `LoanTemplate` comes back, screen renders the same product list it always does.
3. **Loan Product Details / Loan Apply / Upload Docs** — each screen reads `providerId` off its own route and forwards it to the next, same as `clientId`.
4. **Confirm Details screen** — on submit, calls `loansRepository.submitLoanApplication(providerId = "sbi", ...)` → resolves to `DummyLoanProvider` → returns a mock success `DataState.Success(...)`.

If `FineractLoanProvider` were registered under `"sbi"` instead of the shared dummy, step 2 and 4
above would transparently start hitting the real Fineract API — **no changes needed in any
screen or ViewModel**, only in the one map entry inside `LoanApplicationModule.kt`.

---

## The library's own network engine

`libs/mifos-loans` does not depend on the host app's `core/network` module for HTTP anymore.
`network/di/LoansNetworkModule.kt` builds its own `Ktorfit` instance (qualifier `"LoansClient"`),
using the same generic building blocks the app's own client uses:

- `httpClient()` / `setupDefaultHttpClient()` / `DynamicBaseUrlPlugin` / `DynamicLoggableHosts` — generic Ktor plumbing from `core-base:network`
- `KtorInterceptor` reading `UserPreferencesRepository.authToken` — so it still authenticates against the same logged-in session, just via its own client instance
- `UrlType.SELF_SERVICE` — same Fineract self-service base URL the rest of the app uses

`core/network` no longer contains any loan-specific code (`LoansService`, loan DTOs,
`ApiEndPoints.LOANS` were all removed from it).

---

## Current state — what's real vs. placeholder

| Piece | Status |
|---|---|
| 6-screen nav chain, `providerId` threading | Real, verified on-device |
| `LoanProviderRegistry` / `LoansRepository` resolution mechanism | Real |
| `DummyLoanProvider` bound for all 4 UI provider ids | Active (intentional — "no APIs for now") |
| `FineractLoanProvider` + library's own Ktorfit engine | Built, compiles, registered under `"fineract"` — **not** wired to any UI-selectable provider |
| Passcode/biometric gate before submit | Disabled (commented out in `ConfirmDetailsViewModel`, easy to restore) |
| Fineract endpoint shapes (`loans/template?templateType=individual`, `POST loans`) | Ported as-is from mifos-mobile — **do not yet match** the simpler ticket-specified shape (`GET /self/loanproducts`, `POST /self/loans`, `GET /self/loans/{id}`, `POST /self/loans/{id}/transactions`) — deferred by request |

## To activate a real provider later

1. Build a concrete `LoanProvider` implementation (can reuse `FineractLoanProvider` if the ticket's
   simpler endpoint shape is implemented on `LoansService`, or write a new adapter class in `providers/`).
2. Bind it in `LoanApplicationModule.kt` and swap the relevant entry in `LoanProviderRegistry`'s
   `providersById` map (e.g. `"sbi" to get<SbiLoanProvider>()` instead of `"sbi" to dummy`).
3. Nothing else changes — no screen, ViewModel, or route touches a concrete provider type directly.
