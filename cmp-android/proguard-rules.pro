# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-keep class * extends androidx.room.RoomDatabase { <init>(); }

# Security module — keep expect/actual classes that use reflection or JNI
-keep class template.core.base.security.FieldEncryptor { *; }
-keep class template.core.base.security.SecureKeyProvider { *; }
-keep class template.core.base.security.SecureRandom { *; }
-keep class template.core.base.security.TamperDetector { *; }
-keep class template.core.base.security.SecureWiper { *; }
-keep class template.core.base.security.BiometricAuthenticator { *; }

# Keep TypeConverter classes used by Room via annotation
-keep class org.mifos.core.database.utils.ChargeTypeConverters { *; }

# Keep SensitiveString — zeroing depends on exact CharArray field layout
-keep class template.core.base.security.SensitiveString { *; }

# Prevent obfuscation of security enums used in when-branches
-keepclassmembers enum template.core.base.security.** { *; }

# BouncyCastle security provider (desktop JVM, also included in Android classpath)
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
