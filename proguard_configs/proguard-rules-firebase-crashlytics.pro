##---------------Begin: proguard configuration for Crashlytics  ----------
-keepattributes SourceFile,LineNumberTable
#TODO: investigate why exceptions are not obfuscated
-keepclassmembers,allowobfuscation public class * extends java.lang.Exception
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**
##---------------End: proguard configuration for Crashlytics  ----------