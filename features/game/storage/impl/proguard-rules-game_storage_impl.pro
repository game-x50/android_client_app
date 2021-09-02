# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class com.ruslan.hlushan.game.storage.impl.remote.dto.server.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.ruslan.hlushan.game.storage.impl.remote.dto.server.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.ruslan.hlushan.game.storage.impl.remote.dto.server.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}