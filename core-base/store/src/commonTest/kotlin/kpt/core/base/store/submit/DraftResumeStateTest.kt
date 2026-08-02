/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.submit

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class DraftResumeStateTest {

    private val formKey = "test_form"

    // ─── T1: no pending → None ────────────────────────────────────────────────

    @Test
    fun `resumeStateFor emits None when no draft exists`() = runTest {
        val outbox = FakeSubmitOutbox<String>()
        val state = outbox.resumeStateFor(formKey).first()
        assertIs<DraftResumeState.None>(state)
    }

    // ─── T2: pending draft → HasDraft ────────────────────────────────────────

    @Test
    fun `resumeStateFor emits HasDraft when PENDING entry exists`() = runTest {
        val outbox = FakeSubmitOutbox<String>()
        outbox.save(formKey, "saved_payload")

        val state = outbox.resumeStateFor(formKey).first()
        val hasDraft = assertIs<DraftResumeState.HasDraft<String>>(state)
        assertEquals("saved_payload", hasDraft.entry.payload)
        assertEquals(formKey, hasDraft.entry.formKey)
    }

    // ─── T3: flow transitions None → HasDraft on save ────────────────────────

    @Test
    fun `resumeStateFor transitions from None to HasDraft when draft is saved`() = runTest {
        val outbox = FakeSubmitOutbox<String>()
        val flow = outbox.resumeStateFor(formKey)

        val initial = flow.first()
        assertIs<DraftResumeState.None>(initial)

        outbox.save(formKey, "payload")
        val after = flow.first()
        assertIs<DraftResumeState.HasDraft<String>>(after)
    }

    // ─── T4: SUBMITTED entry → None (not surfaced as draft) ──────────────────

    @Test
    fun `resumeStateFor emits None when only SUBMITTED entries exist`() = runTest {
        val outbox = FakeSubmitOutbox<String>()
        val id = outbox.save(formKey, "payload")
        outbox.markSubmitted(id)

        val state = outbox.resumeStateFor(formKey).first()
        assertIs<DraftResumeState.None>(state)
    }

    // ─── T5: RETRYING entry → None (claimed by syncer, not for UI) ───────────

    @Test
    fun `resumeStateFor emits None when entry is RETRYING`() = runTest {
        val outbox = FakeSubmitOutbox<String>()
        val id = outbox.save(formKey, "payload")
        outbox.markRetrying(id)

        val state = outbox.resumeStateFor(formKey).first()
        assertIs<DraftResumeState.None>(state)
    }
}
