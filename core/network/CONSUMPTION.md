# Consuming `core/network` in a feature

> The remote access layer: Ktorfit API interfaces + Koin-injected per-API config classes that
> carry a `baseUrl` (fork-overridable). This is the FIRST link of any `NETWORK_*` / `*_WITH_CACHE`
> read path — the fetcher you hand to `core/store`.

## Call sequence

1. **Add a config class** carrying a fork-overridable `baseUrl` (and an optional key), defaulting
   to the public production endpoint — mirror `FredApiConfig(apiKey, baseUrl = DEFAULT_BASE_URL)`.
   Register your named endpoint in `AppUrlTypes` if the app switches base URLs at runtime.
2. **Declare a Ktorfit API interface** with suspend endpoint functions returning your DTOs (see
   `core/model` + the DTOs under `demo/**/dto/`), e.g. `FredApi`, `CoinGeckoApi`, `FrankfurterApi`.
3. **Wire it in `NetworkModule`** (Koin): build the `Ktorfit` with
   `httpClient(setupDefaultHttpClient(baseUrl = get<YourApiConfig>().baseUrl, loggableHosts = ...))`
   — `setupDefaultHttpClient` (from `core-base/network`) applies JSON, logging, and the optional
   `DynamicBaseUrlPlugin` for runtime URL switching.
4. **Expose the API as a `single<YourApi>`** so `core/data` / `core/store` can inject it as the
   Store5 `Fetcher` source. (Read stores wrap it in `Fetcher.of { ... api.call() ... }`.)

## Notes

- Backend URLs are NEVER hardcoded in a store — they flow from the injected config `single { }`,
  which a fork overrides at its app-module to swap in mocks / mirrors / per-environment endpoints.
- `jsonplaceholder` (`JsonPlaceholderApi`) is the only WRITABLE demo backend — it backs the
  `MUTABLE` archetype's `Updater` (`PUT /todos/{id}`).

Canonical example: feature/crypto (CoinGecko read) and feature/currency-rates (Frankfurter read).

Symbols: NetworkModule, AppUrlTypes, FredApiConfig, FrankfurterApiConfig, WorldBankApiConfig, FintechApiClient, CoinGeckoApi, FredApi, FrankfurterApi, JsonPlaceholderApi
