#!/usr/bin/env bash
#
# embed-xcframework.sh — flavor-aware KMP framework embed for Xcode (E6).
#
# Invoked as an Xcode Run-Script build phase on the `iosApp` target (see
# iosApp.xcodeproj/project.pbxproj → PBXShellScriptBuildPhase "[KMP] Embed and
# Sign ComposeApp XCFramework"). It runs BEFORE Compile Sources so the exported
# `ComposeApp` framework is built, code-signed and copied into the app bundle for
# every build — the SwiftPM/XCFramework replacement for the CocoaPods
# `[CP] Embed Pods Frameworks` phase this project used to carry.
#
# ── Flavor-aware build-type mapping (the piece the CocoaPods plugin used to do) ──
# The old `cmp-shared/build.gradle.kts` `cocoapods { xcodeConfigurationToNativeBuildType[…] }`
# block mapped every `{flavor}{BuildType}` Xcode configuration (demoDebug, demoStaging,
# demoRelease, prodDebug, prodStaging, prodRelease — from the kmp-product-flavors DSL)
# to a Kotlin/Native build type: a *debuggable* build type → DEBUG, everything else
# → RELEASE. We reproduce EXACTLY that here from Xcode's `$CONFIGURATION`:
#     *Debug  -> Debug   (debuggable)
#     *        -> Release (Staging + Release are optimized/non-debuggable)
# The KMP `embedAndSignAppleFrameworkForXcode` task only understands the canonical
# `Debug`/`Release` configuration names, so we hand it the mapped name via the
# `CONFIGURATION` env var — that is what preserves per-variant iOS builds after the
# CocoaPods removal.
set -euo pipefail

# SRCROOT is exported by Xcode (…/cmp-ios). The Gradle wrapper lives at the repo root,
# one level up from cmp-ios. Fall back to a relative resolve when run outside Xcode.
SRCROOT="${SRCROOT:-$(cd "$(dirname "$0")/.." && pwd)}"
REPO_ROOT="$(cd "$SRCROOT/.." && pwd)"
GRADLEW="$REPO_ROOT/gradlew"

CONFIG="${CONFIGURATION:-prodRelease}"
case "$CONFIG" in
  *Debug|*debug) KOTLIN_BUILD_TYPE="Debug" ;;   # debuggable → DEBUG slice
  *)             KOTLIN_BUILD_TYPE="Release" ;;  # Staging + Release → RELEASE slice
esac

echo "note: [KMP] embed-xcframework — Xcode CONFIGURATION=$CONFIG → Kotlin build type $KOTLIN_BUILD_TYPE"

if [ ! -x "$GRADLEW" ]; then
  echo "error: gradlew not found or not executable at $GRADLEW" >&2
  exit 1
fi

# The app consumes ComposeApp as a SwiftPM BINARY TARGET (cmp-ios/Package.swift → the XCFramework at
# cmp-shared/build/XCFrameworks/<type>/ComposeApp.xcframework). Kotlin REJECTS
# `embedAndSignAppleFrameworkForXcode` — the direct-integration task — the moment the Xcode project
# carries SwiftPM dependencies ("error: You have SwiftPM dependencies with embedAndSign integration"),
# which it now does: the native Firebase SDK + the ComposeApp binary target itself. So we ASSEMBLE the
# XCFramework the binary target points at; Xcode/SwiftPM embed + sign the binary product automatically
# when resolving the package. This runs BEFORE Compile Sources so the freshly-built framework is what
# links. (Debug/Release maps from Xcode's flavored $CONFIGURATION above.)
"$GRADLEW" -p "$REPO_ROOT" \
  ":cmp-shared:assembleComposeApp${KOTLIN_BUILD_TYPE}XCFramework"

# Stage the SDK-matching `.framework` slice OUT of the freshly-assembled XCFramework into the
# project's `FRAMEWORK_SEARCH_PATHS`
#   $(SRCROOT)/../cmp-shared/build/xcode-frameworks/$(CONFIGURATION|KMPF_VARIANT)/$(SDK_NAME)
# — the app links `ComposeApp` via those search paths (it is NOT a SwiftPM package product; only the
# Firebase SDK is), so this is what makes `import ComposeApp` resolve at LINK time now that embedAndSign
# (which used to write that dir) is gone. Assemble-then-stage works for every flavor because $CONFIG is
# the raw Xcode configuration. (bash 3.2 on the Xcode runner: lowercase via tr, no ${x,,}.)
SDK="${SDK_NAME:-iphoneos}"
TYPE_DIR="$(printf '%s' "$KOTLIN_BUILD_TYPE" | tr '[:upper:]' '[:lower:]')"
XCF="$REPO_ROOT/cmp-shared/build/XCFrameworks/$TYPE_DIR/ComposeApp.xcframework"
case "$SDK" in
  *simulator*) SRC_FW="$(ls -d "$XCF"/*simulator*/ComposeApp.framework 2>/dev/null | head -1)" ;;
  *)           SRC_FW="$(ls -d "$XCF"/ios-arm64/ComposeApp.framework 2>/dev/null | head -1)" ;;
esac
DST_DIR="$REPO_ROOT/cmp-shared/build/xcode-frameworks/$CONFIG/$SDK"
if [ -n "${SRC_FW:-}" ] && [ -d "$SRC_FW" ]; then
  echo "note: [KMP] staging $(basename "$(dirname "$SRC_FW")") slice → xcode-frameworks/$CONFIG/$SDK for FRAMEWORK_SEARCH_PATHS"
  mkdir -p "$DST_DIR"
  rm -rf "${DST_DIR:?}/ComposeApp.framework"
  cp -a "$SRC_FW" "$DST_DIR/"
else
  echo "warning: [KMP] no ComposeApp slice for SDK=$SDK under $XCF — FRAMEWORK_SEARCH_PATHS may be unset" >&2
fi
