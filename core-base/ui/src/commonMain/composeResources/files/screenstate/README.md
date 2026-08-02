# Default ScreenState Lottie Animations

This directory ships the default Lottie animations referenced by
`template.core.base.ui.DefaultLottieAnimations`. Apps that adopt the defaults via
`ScreenStateVisual.Lottie(spec = DefaultLottieAnimations.empty)` will load these files
at runtime.

## Required files

| File | Used by | Suggested style |
|---|---|---|
| `empty.json` | `DefaultLottieAnimations.empty` | brand-neutral empty box / inbox |
| `error.json` | `DefaultLottieAnimations.error` | warning / broken-page illustration |
| `no_network.json` | `DefaultLottieAnimations.noNetwork` | disconnected wifi / cloud-off |
| `loading.json` | `DefaultLottieAnimations.loading` | spinner alternative (optional) |

## Sourcing

Files MUST be JSON-format Lottie animations (not `.lottie` zip bundles — those need
the `compottie-dot-lottie` artifact, which this module does not pull in).

Recommended source: [LottieFiles.com](https://lottiefiles.com/) free MIT-licensed
animations. Pick small, polished, brand-neutral pieces — typical file size 5–30 KB.

Document attribution for each file in this README when added (`<file> — <author> via
LottieFiles, license: <id>`).

## Status

> **Not yet shipped.** Until the four JSON files land here, calling
> `DefaultLottieAnimations.{empty,error,noNetwork,loading}` will throw
> `MissingResourceException` at runtime. Apps can still use `ScreenStateVisual.Lottie`
> with their own `spec` lambda, or fall back to `ScreenStateVisual.Vector` (the
> library default).
