# C4GT 2025 – Project Documentation

## Overview
This document summarizes my contributions during the C4GT 2025 program under the Mifos Initiative.  
I worked on two main projects:
- **Android Client (openmf/android-client)** – Migrating feature modules to **Compose Multiplatform (CMP)**.
- **Mobile Wallet / Mifos Pay (openmf/mobile-wallet)** – Implementing **UPI payment flows** and setting up future modules (e.g., AutoPay, Bank Transfers, Customer/ Merchant Payments).

---

## 1. Android Client (openmf/android-client)

### Scope
- Migrated feature modules to **Compose Multiplatform**:
    - `feature/checker-inbox-task`
    - `feature/path-tracking`
    - `feature/client` (largest module, 13 sub-features)
- Bug fixing and improvements in sub-features (`client-identifiers`, `client-charges`, `client-pinpoint`, etc.).
- Reviewed multiple PRs from the team.

### Key Achievements
- **Full migration** of `feature/client` to CMP (merged).
- **Bug fixes** across sub-features: dialogs, UI states, silent failures, signature reset, charge dialog UX.
- **Abstractions & design ideas** for Android SDK dependencies (LocationManager, File, Uri, Context).
- **PR reviews** across the repo, helping maintain code quality.

### Selected Pull Requests
- [#2389](https://github.com/openMF/android-client/pull/2389) – Migrate checker-inbox-task to CMP
- [#2400](https://github.com/openMF/android-client/pull/2400) – Migrate path-tracking to CMP
- [#2401](https://github.com/openMF/android-client/pull/2401) – Migrate client module to CMP
- [#2415](https://github.com/openMF/android-client/pull/2415) – Fix identifiers empty state & dialog layout
- [#2430](https://github.com/openMF/android-client/pull/2430) – Merge ViewModels, add charge validation
- [#2433](https://github.com/openMF/android-client/pull/2433) – Fix Client Pinpoint & implement Pinpoint Map dialog

_(More PRs linked in weekly updates)_

### Learnings
- Large-scale CMP migrations (navigation, DI, tightly-coupled Android SDK APIs).
- Debugging CMP-related UI bugs and state issues.
- Collaboration via reviews, navigation design, and Jira ticketing.

---

## 2. Mobile Wallet / Mifos Pay (openmf/mobile-wallet)

### Scope
- **UPI Payment Flows**:
    - Scan & Pay (via QR codes)
    - Pay to UPI ID / Pay Anyone
    - Bank Transfers (self & others)
    - AutoPay module setup
- **Standing Instructions**:
    - Fixed loading issues and added fallbacks for missing API data.
- **Upgrades & Setup**:
    - Upgraded compileSdk, AGP, Gradle, and dependencies.
    - Drafted implementation plan for **Merchant Payments KMP Module**.

### Key Achievements
- Built **frontend flows** for UPI: navigation, screen layouts, and UI/UX fixes.
- Implemented **QR-based UPI app invocation** (draft PR).
- Drafted backend design assumptions with **PHEE + Fineract + PSP bank UPI APIs**.
- Fixed **Standing Instruction bugs** (merged).
- Created Jira tickets for merchant payments and loan sandbox module.

### Selected Pull Requests
- [#1889](https://github.com/openMF/mobile-wallet/pull/1889) – Update compileSdk to 35, AGP, Gradle, dependencies
- [#1890](https://github.com/openMF/mobile-wallet/pull/1890) – Fix SI loading issue
- [#1891](https://github.com/openMF/mobile-wallet/pull/1891) – Add default values for missing fields
- [#1899](https://github.com/openMF/mobile-wallet/pull/1899) – Fix DialogState serialization error
- [#1903](https://github.com/openMF/mobile-wallet/pull/1903) – Add basic UPI payment support (draft)
- [#1906](https://github.com/openMF/mobile-wallet/pull/1906) – Implement Pay Anyone (draft)
- [#1907](https://github.com/openMF/mobile-wallet/pull/1907) – Add Bank Transfers (draft)
- [#1908](https://github.com/openMF/mobile-wallet/pull/1908) – Setup AutoPay feature (draft)
- [#1909](https://github.com/openMF/mobile-wallet/pull/1909) - implement autopay dashboard (draft)
- [#1910](https://github.com/openMF/mobile-wallet/pull/1910) - biller and bills setup (draft)
- [#1912](https://github.com/openMF/mobile-wallet/pull/1912) - see all; sorting; empty state; per-tab scroll; top bar
- [#1913](https://github.com/openMF/mobile-wallet/pull/1913) - resolve overlapping screens during transitions

### Learnings
- UPI payment flow design & integration patterns.
- Mapping frontend flows to backend orchestration.
- Drafting extensible KMP modules (Add Payments, Merchant Payments, Loan Sandbox).
- Handling cross-module UI/UX refinements.

---

## 3. Weekly Progress
Detailed week-by-week updates are available [here](https://github.com/openMF/mobile-wallet/issues/1852#issuecomment-2950151917).  
(Summarized in PRs above; includes Week 1 → Week 14 breakdown.)

---

## 4. Current Status
- **Android Client**: CMP migration PRs merged, bug fixes completed.
- **Mobile Wallet**: UPI payment flow and AutoPay PRs in draft/review; backend clarifications pending.
- **Documentation**: Jira tickets created and parented under master tickets.

---

## 5. Next Steps
For future contributors:
- Complete review & merge of draft PRs in `mobile-wallet`.
- Connect UPI frontend flows with backend APIs once integration clarifications are resolved.
- Extend Payments and AutoPay modules as planned.

---

## 6. License
This documentation is licensed under the  
[Creative Commons Attribution 4.0 International License (CC BY 4.0)](./LICENSE.md).

You are free to share and adapt the material, provided you give appropriate credit.

## 7. References
- Jira tickets: [MW-211](https://mifosforge.jira.com/browse/MW-211)
- Midterm presentation: [Biplab_Dutta_C4GT_2025_Midpoint_Slides.pdf](https://github.com/biplab1/c4gt-2025-mifos-docs/blob/main/Biplab_Dutta_C4GT_2025_Midpoint_Slides.pdf)
- [Mifos Initiative](https://mifos.org/)
- [C4GT Program](https://codeforgovtech.in/)
- [Mobile Wallet Repository](https://github.com/openMF/mobile-wallet)
- [Android Client Repository](https://github.com/openMF/android-client)

---

