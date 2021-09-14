# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class com.ruslan.hlushan.core.language.impl.dto.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.ruslan.hlushan.core.language.impl.dto.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.ruslan.hlushan.core.language.impl.dto.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}