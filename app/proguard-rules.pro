# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}
-keepnames class * implements dagger.hilt.internal.GeneratedComponent
-dontwarn dagger.hilt.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.TypeConverter
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Coroutines
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Material Compose
-keep class com.google.android.material.** { *; }

# Keep all Composable functions
-keep class androidx.compose.** { *; }

# Security Crypto
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.**

# General
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''