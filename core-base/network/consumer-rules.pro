# Network module consumer ProGuard rules — automatically applied to consuming modules

# Ktor's IntellijIdeaDebugDetector references JVM-only java.lang.management.* (absent on
# Android). Safe to ignore — used only for debugger detection (returns false on Android).
# Without these, :cmp-android:minifyReleaseWithR8 fails on every consuming app's
# release build (R8 "Missing class java.lang.management.ManagementFactory").
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
