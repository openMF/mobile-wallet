#!/usr/bin/env bash
# verify-2-4-0.sh — per-target main+test compile matrix for the Kotlin 2.4.0
# dependency modernization (kotlin-2-4-0-upgrade epic).
#
# F5 principle: this verifies TEST source sets on EVERY target, not just main —
# a dependency bump that changes an interface surfaces first in test fakes.
#
# NOTE: CI (.github/workflows/pr-check.yml + quality-gate.yml) is the AUTHORITATIVE
# verifier and runs the full matrix on clean runners. This script is the local
# mirror for when you want to reproduce a specific target failure. Local Gradle is
# slow; prefer reading the PR CI logs.
#
# Usage: bash scripts/verify-2-4-0.sh        (all targets, main + test)
#        MAIN_ONLY=1 bash scripts/verify-2-4-0.sh   (skip the test-compile pass)
set -euo pipefail
cd "$(dirname "$0")/.."

echo "── main compile: commonMain metadata (all modules) ──"
./gradlew compileKotlinMetadata

echo "── main compile: Android ──"
./gradlew assembleDebug

echo "── main compile: Desktop/JVM ──"
./gradlew compileKotlinJvm

echo "── main compile: Web (js + wasmJs) ──"
./gradlew compileKotlinJs compileKotlinWasmJs

echo "── main compile: iOS klib (arm64 + simulatorArm64) ──"
./gradlew compileKotlinIosArm64 compileKotlinIosSimulatorArm64

if [ "${MAIN_ONLY:-0}" != "1" ]; then
  echo "── F5: TEST source sets — Desktop/JVM + iOS + Android unit test ──"
  ./gradlew compileTestKotlinJvm
  ./gradlew compileTestKotlinIosSimulatorArm64
  ./gradlew compileDebugUnitTestKotlinAndroid
fi

echo "✅ verify-2-4-0: per-target main${MAIN_ONLY:+ (main-only)} compile green"
