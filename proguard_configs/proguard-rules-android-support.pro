##---------------Begin: proguard configuration for android support library  ----------
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
##---------------End: proguard configuration for android support library  ----------