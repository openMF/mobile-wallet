/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.screen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Verifies the data contract of [ScreenStateDefaults] and its nested config types.
 *
 * Compose-runtime behavior (full CompositionLocal resolution chain, override-tier ordering
 * inside [ScreenContent], `LaunchedEffect`-once semantics for `onShown`, animated state
 * transitions) is verified separately via UI tests once multiplatform Compose UI test
 * infrastructure (`compose.uiTest` in commonTest) is wired into this module. The tests
 * here cover the underlying data plumbing those UI tests would assert on.
 */
class ScreenStateDefaultsTest {

    // ── Default construction ──────────────────────────────────────────────

    @Test
    fun screenStateDefaults_defaults_areSensibleAndStable() {
        val a = ScreenStateDefaults()
        val b = ScreenStateDefaults()
        assertEquals(a, b, "Default-constructed instances must be equal (data class contract).")
    }

    @Test
    fun screenStateLoading_defaultIsSpinner() {
        assertSame(ScreenStateLoading.Spinner, ScreenStateDefaults().loading)
    }

    @Test
    fun screenStateEmpty_defaults() {
        val empty = ScreenStateEmpty()
        assertEquals("No data available", empty.message)
        assertNull(empty.title)
        assertNull(empty.cta)
        assertTrue(empty.visual is ScreenStateVisual.Vector)
    }

    @Test
    fun screenStateError_defaults() {
        val error = ScreenStateError()
        assertEquals("Try again", error.retryText)
        assertNull(error.title)
        assertNull(error.onShown)
        assertTrue(error.visual is ScreenStateVisual.Vector)
        // Default messageFor falls back to throwable.message, then a generic string.
        assertEquals("boom", error.messageFor(IllegalStateException("boom")))
        assertEquals("Something went wrong", error.messageFor(IllegalStateException()))
    }

    @Test
    fun screenStateNoNetwork_defaults() {
        val nn = ScreenStateNoNetwork()
        assertEquals("You're offline", nn.message)
        assertEquals("Sign in to your WiFi network", nn.captivePortalMessage)
        assertEquals("Try again", nn.retryText)
        assertTrue(nn.visual is ScreenStateVisual.Vector)
        assertTrue(nn.captivePortalVisual is ScreenStateVisual.Vector)
    }

    // ── Override surface ──────────────────────────────────────────────────

    @Test
    fun screenStateError_onShownCallback_storedAsProvided() {
        val captured = mutableListOf<Throwable>()
        val cfg = ScreenStateError(onShown = { captured.add(it) })
        val err = RuntimeException("x")
        assertNotNull(cfg.onShown).invoke(err)
        assertEquals(listOf<Throwable>(err), captured)
    }

    @Test
    fun screenStateEmpty_ctaIsNullByDefaultButCarriesData() {
        val cta = ScreenStateCta(label = "Add new") { /* no-op */ }
        val cfg = ScreenStateEmpty(cta = cta)
        assertEquals("Add new", cfg.cta?.label)
    }

    @Test
    fun screenStateLoading_skeletonRowCount() {
        val skel = ScreenStateLoading.Skeleton(rowCount = 7)
        assertEquals(7, skel.rowCount)
    }

    @Test
    fun screenStateLoading_skeletonDefaultRowCount() {
        assertEquals(3, ScreenStateLoading.Skeleton().rowCount)
    }

    // ── Visual sealed type ────────────────────────────────────────────────

    @Test
    fun screenStateVisual_whenIsExhaustive() {
        // Exhaustive `when` over the sealed type compiles without `else` — sealed contract.
        // Default constructors of ScreenStateDefaults provide a Vector visual we can inspect.
        val visuals: List<ScreenStateVisual> = listOf(
            ScreenStateDefaults().empty.visual,
            ScreenStateDefaults().error.visual,
            ScreenStateDefaults().noNetwork.visual,
            ScreenStateDefaults().noNetwork.captivePortalVisual,
            ScreenStateVisual.Lottie(spec = { error("not invoked in this test") }),
            ScreenStateVisual.Custom { /* no-op */ },
        )
        for (v in visuals) {
            val tag: String = when (v) {
                is ScreenStateVisual.Vector -> "vector"
                is ScreenStateVisual.PainterRef -> "painter"
                is ScreenStateVisual.Lottie -> "lottie"
                is ScreenStateVisual.Custom -> "custom"
            }
            assertNotNull(tag)
        }
    }

    @Test
    fun screenStateVisual_lottie_defaultsAreSensible() {
        val lottie = ScreenStateVisual.Lottie(spec = { error("not invoked in this test") })
        assertEquals(io.github.alexzhirkevich.compottie.Compottie.IterateForever, lottie.iterations)
        assertEquals(1f, lottie.speed)
    }

    @Test
    fun screenStateDefaults_immutableViaCopy() {
        val original = ScreenStateDefaults()
        val customized = original.copy(
            empty = ScreenStateEmpty(message = "Nothing yet"),
        )
        assertEquals("No data available", original.empty.message, "Copy must not mutate original.")
        assertEquals("Nothing yet", customized.empty.message)
    }

    // ── CompositionLocal contract ─────────────────────────────────────────

    @Test
    fun localScreenStateDefaults_exists() {
        // The CompositionLocal MUST exist as a public symbol so app modules can
        // `CompositionLocalProvider(LocalScreenStateDefaults provides ...)`.
        // Full Compose-runtime resolution (default-factory invocation, provided-value
        // override) is exercised by UI tests once compose.uiTest is wired in.
        assertNotNull(LocalScreenStateDefaults)
    }

    // ── Override surface — config-level (data contract behind UI override tiers) ───

    @Test
    fun screenStateError_customMessageFor_invokedWithExactThrowable() {
        val seen = mutableListOf<Throwable>()
        val cfg = ScreenStateError(
            messageFor = { t ->
                seen += t
                "user-message: ${t::class.simpleName}"
            },
        )
        val err = IllegalArgumentException("boom")
        val rendered = cfg.messageFor(err)
        assertEquals("user-message: IllegalArgumentException", rendered)
        assertEquals(listOf<Throwable>(err), seen, "messageFor must receive the original throwable instance.")
    }

    @Test
    fun screenStateDefaults_copyOnNestedConfig_preservesUnchangedSiblings() {
        val original = ScreenStateDefaults()
        val customized = original.copy(
            error = original.error.copy(retryText = "Reload"),
        )
        assertEquals("Reload", customized.error.retryText)
        assertEquals("Try again", original.error.retryText, "Original must not mutate.")
        assertSame(original.empty, customized.empty, "Sibling configs share reference when not changed.")
        assertSame(original.loading, customized.loading)
        assertSame(original.noNetwork, customized.noNetwork)
    }

    @Test
    fun screenStateError_titleAndMessageRenderingContract() {
        // Default*Content reads `title` (M3 titleMedium) above `messageFor(error)` (bodyLarge).
        // Verify the data wiring: title is null by default and a custom title round-trips.
        val defaultCfg = ScreenStateError()
        assertNull(defaultCfg.title, "title is opt-in by default")
        val titled = defaultCfg.copy(title = "Couldn't load")
        assertEquals("Couldn't load", titled.title)
    }

    // ── Default messageFor routes through categorize() (Phase 2.6) ────────

    @Test
    fun defaultMessageFor_networkError_returnsNetworkCopy() {
        val err = RuntimeException("connection failed", IOExceptionLike("Connect timed out"))
        assertEquals(
            "Can't reach the server. Check your connection.",
            ScreenStateError().messageFor(err),
        )
    }

    @Test
    fun defaultMessageFor_authError_returnsAuthCopy() {
        val err = RuntimeException("HTTP 401: Unauthorized")
        assertEquals(
            "Your session expired. Please sign in again.",
            ScreenStateError().messageFor(err),
        )
    }

    @Test
    fun defaultMessageFor_serverError_returnsServerCopy() {
        val err = RuntimeException("HTTP 503: Service Unavailable")
        assertEquals(
            "Our servers are having a moment. Try again in a bit.",
            ScreenStateError().messageFor(err),
        )
    }

    @Test
    fun defaultMessageFor_genericError_fallsBackToThrowableMessage() {
        // Same as the original contract: nothing matches → return the throwable's message.
        assertEquals("boom", ScreenStateError().messageFor(IllegalStateException("boom")))
        assertEquals("Something went wrong", ScreenStateError().messageFor(IllegalStateException()))
    }

    /** Throwable whose simpleName matches the network heuristic ("IOException"). */
    private class IOExceptionLike(message: String) : RuntimeException(message)
}
