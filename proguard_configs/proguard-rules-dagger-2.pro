##---------------Begin: proguard configuration for Dagger2  ----------
-keepattributes *Annotation*
-keepattributes *Provides*
-keepattributes *Singleton*
-keepattributes *Inject*
-keepattributes *Module*
-keepattributes *Named*
-keep @interface dagger.*
-keep @dagger.Module class *
-keepclassmembers class * { @javax.inject.* <fields>; }
-keepclasseswithmembernames class * { @javax.inject.* <fields>; }
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection
-keep class dagger.** { *; }
-dontwarn dagger.**
-dontwarn com.google.errorprone.annotations.**
-keep class com.google.errorprone.annotations.** { *; }
##---------------End: proguard configuration for Dagger2  ----------