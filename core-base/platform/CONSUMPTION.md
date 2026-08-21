# Consuming `core-base/platform`

> Framework-shared `expect`/`actual` platform bridges — no sibling `core/platform` wrapper exists; the
> app root wires `LocalManagerProvider` directly.

## Call sequence

1. Wrap the app root with `LocalManagerProvider(LocalContext.current) { ... }` — it provides
   `LocalAppReviewManager`, `LocalIntentManager`, and `LocalAppUpdateManager` to the whole composition
   tree via `CompositionLocal`. The shipped call site is `cmp-shared/src/commonMain/kotlin/cmp/shared/SharedApp.kt`.
2. Read a manager from any composable, e.g. `val intentManager = LocalIntentManager.current`. Use
   `IntentManager.shareText` / `shareFile` / `launchUri` for platform sharing, and
   `getShareDataFromIntent(intent)` to handle content shared *into* the app.
3. Trigger the platform review prompt with `LocalAppReviewManager.current.promptForReview()`; check for
   and resume app updates with `LocalAppUpdateManager.current.checkForAppUpdate()` /
   `checkForResumeUpdateState()` (call the latter from your root Activity's `onResume`).
4. Inject `GarbageCollectionManager` (bound in `platformModule` as `single<GarbageCollectionManager>`)
   and call `tryCollect()` after freeing large resources (e.g. large bitmap/file buffers).

## Notes

- `MimeType.fromExtension(ext)` / `fromFileName(name)` resolve a MIME type for `IntentManager.shareFile`.
- Non-Android platforms (Desktop/JS/Native/WasmJs) ship placeholder `IntentManagerImpl` /
  `AppReviewManagerImpl` / `AppUpdateManagerImpl` bodies (most methods are no-ops or `TODO`) — extending
  those actuals for real per-platform behavior is a framework-level change, not a per-fork override seam.
- `AppContext` / `LocalContext` is the platform-agnostic Context handle: on Android it's
  `android.content.Context`; elsewhere it's a singleton placeholder object.
- `platformModule` also binds `CoroutineDispatcher` to `Dispatchers.Unconfined` — used by
  `GarbageCollectionManagerImpl`, not a general-purpose dispatcher for feature code.

Canonical example: `cmp-shared/src/commonMain/kotlin/cmp/shared/SharedApp.kt` wraps the whole app with
`LocalManagerProvider`.

Symbols: LocalManagerProvider, LocalIntentManager, LocalAppReviewManager, LocalAppUpdateManager, IntentManager, GarbageCollectionManager, MimeType
