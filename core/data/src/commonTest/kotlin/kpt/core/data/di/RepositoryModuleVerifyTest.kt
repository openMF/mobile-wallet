/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(org.koin.core.annotation.KoinInternalApi::class)

package kpt.core.data.di

import kpt.core.base.store.submit.SubmitOutbox
import org.koin.core.qualifier.Qualifier
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Verifies that every `SubmitOutbox<T>` declared in [DataModule] is registered
 * under its corresponding [OutboxQualifiers] entry. Catches regressions
 * where a new payload type is added to [OutboxQualifiers] but the matching
 * `single<>` registration is missing — or vice versa.
 *
 * Failure mode if not caught: `ClassCastException` at first
 * `.saveByUniqueKey(payload)` call on the mis-registered outbox.
 *
 * Implementation note: we introspect [DataModule]'s `mappings` table (the
 * static registration map) rather than calling `koinApplication { modules(DataModule) }
 * .koin.get<...>()`. Eager-instantiation of the full DataModule graph requires
 * platform actuals (`platformModule`, `platformSecurityModule`, `AppDatabase`)
 * that are not on the classpath in `:core:data:commonTest`. Static introspection
 * checks the exact contract — "is the qualifier wired to a `SubmitOutbox`
 * primary type?" — without paying that cost.
 *
 * NOTE: PriceAlert is still verified — the Alerts archival decision is
 * deferred (see `feature/_archive/alerts/README.md`), so the qualifier and
 * its outbox binding remain active in `RepositoryModule.kt` until the
 * 2026-08-23 deadline.
 */
class RepositoryModuleVerifyTest {

    @Test
    fun loanOutboxIsRegisteredUnderLoanQualifier() {
        assertSubmitOutboxBoundUnderQualifier(OutboxQualifiers.Loan)
    }

    @Test
    fun billReminderOutboxIsRegisteredUnderBillReminderQualifier() {
        assertSubmitOutboxBoundUnderQualifier(OutboxQualifiers.BillReminder)
    }

    @Test
    fun loanCalcScenarioOutboxIsRegisteredUnderLoanCalcScenarioQualifier() {
        assertSubmitOutboxBoundUnderQualifier(OutboxQualifiers.LoanCalcScenario)
    }

    @Test
    fun priceAlertOutboxIsRegisteredUnderPriceAlertQualifier() {
        assertSubmitOutboxBoundUnderQualifier(OutboxQualifiers.PriceAlert)
    }

    /**
     * Asserts that [DataModule]'s mapping table contains exactly one
     * `InstanceFactory` whose [org.koin.core.definition.BeanDefinition] has
     * `primaryType == SubmitOutbox::class` AND `qualifier == expected`.
     */
    private fun assertSubmitOutboxBoundUnderQualifier(expected: Qualifier) {
        val match = DataModule.mappings.values.firstOrNull { factory ->
            val def = factory.beanDefinition
            def.primaryType == SubmitOutbox::class && def.qualifier == expected
        }
        assertNotNull(
            match,
            "DataModule must register a SubmitOutbox<*> single<> under qualifier $expected — " +
                "missing or mis-qualified binding. See OutboxQualifiers KDoc.",
        )
        // Sanity — the qualifier value matches what callers use (e.g. `get(qualifier = X)`).
        assertTrue(
            match.beanDefinition.qualifier == expected,
            "Qualifier mismatch for $expected on bound SubmitOutbox<*>.",
        )
    }
}
