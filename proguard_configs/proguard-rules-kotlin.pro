##---------------Begin: proguard configuration for Kotlin  ----------
# Kotlin: This rule will help you to keep your annotation classes and it won't warn for reflection classes.
-dontwarn kotlin.**
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }

# Kotlin: The consolidated rule for Kotlin android project.
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

#TODO: #PROGUARD_TAG: https://proandroiddev.com/kotlin-cleaning-java-bytecode-before-release-9567d4c63911
#-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
#    public static void checkExpressionValueIsNotNull(...);
#    public static void checkNotNullExpressionValue(...);
#    public static void checkReturnedValueIsNotNull(...);
#    public static void checkFieldIsNotNull(...);
#    public static void checkParameterIsNotNull(...);
#}
##---------------End: proguard configuration for Kotlin  ----------