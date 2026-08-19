/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.platform.notification.bill.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin bindings for the bill-reminder notification surface.
 *
 * Each target supplies its own [kpt.core.platform.notification.bill.BillReminderScheduler]
 * actual — Android needs an `applicationContext`, iOS / desktop / JS / WasmJS need nothing — so
 * the binding is split into a per-platform [Module]. Consumers (e.g. `feature/bills`'s
 * `BillsModule`) `includes(notificationModule)` to pull the right actual into the Koin graph.
 *
 * Lives in `core/platform` (the project layer) rather than `core-base/platform` because
 * bill-reminder scheduling is banking-domain-specific — the framework primitive in
 * `core-base/platform` exposes only the generic
 * [kpt.core.base.platform.notification.NotificationScheduler] contract.
 */
expect val notificationModule: Module
