# Consuming `cmp-web` in a fork

`cmp-web` is the browser app-shell — two nearly-identical entry points, one per Kotlin/JS target,
that both call `initKoin()` and render `cmp-shared`'s `SharedApp` into the page.

## What's here

- **`src/jsMain/kotlin/Application.kt`** — the classic Kotlin/JS entry (`js(IR)` target, output
  `cmp-web.js`). Calls `initKoin()`, restores a `localStorage`-persisted `app_language`, then
  `onWasmReady { ComposeViewport(document.body!!) { ... } }` rendering `SharedApp`. Locale changes
  persist to `localStorage` and reload the page.
- **`src/wasmJsMain/kotlin/Main.kt`** — the `wasmJs` target (output `cmp-wasm.js`). Same shape,
  plus `configureWebResources { resourcePathMapping { "./$path" } }` for Compose resource loading
  under the wasm target.
- **`webpack.config.d/dev-server-headers.js`** — sets COOP/COEP headers on the local dev server so
  `crossOriginIsolated` is `true`, enabling `WebWorkerSQLiteDriver` + OPFS-backed Room storage
  locally (GitHub Pages doesn't send these headers, so production falls back to
  `Room.inMemoryDatabaseBuilder`).

## What a fork touches

- **SEO / link-preview metadata** — `index.html` (`jsMain`/`wasmJsMain/resources/`) is
  token-substituted at build time (`jsProcessResources`/`wasmJsProcessResources` in
  `build.gradle.kts`) from `gradle/fork.properties` — `APP_DISPLAY_NAME`, `APP_DESCRIPTION`
  (`store.android.short.description` / `store.subtitle`), `APP_KEYWORDS`, `APP_COPYRIGHT`,
  `APP_AUTHOR`, `APP_URL`, `OG_IMAGE`. Set these via `app-profile/` (store listing + org fields),
  not by hand-editing `index.html`.
- **App behavior/UI** — add features via the `cmp-navigation` registries, not by editing
  `Application.kt`/`Main.kt`.

## What to leave to sync

Both entry points and the webpack dev-server config are template infrastructure — identical
across forks except for the token-substituted metadata above.

See [`README.md`](README.md) for the module graph, and `cmp-navigation`'s
[`CONSUMPTION.md`](../cmp-navigation/CONSUMPTION.md) for how to add features.

Symbols: main (jsMain), main (wasmJsMain)
