/**
 * @author Ruslan Hlushan on 2019-08-28
 */

object PluginClasspath {

    const val androidGradle = "com.android.tools.build:gradle:${Versions.gradleVersion}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val kotlinxSerilization = "plugin.serialization"
    const val googleServices = "com.google.gms:google-services:${Versions.googlePluginServicesVersion}"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsPluginVersion}"
    const val leakCanaryDeobfuscation = "com.squareup.leakcanary:leakcanary-deobfuscation-gradle-plugin:${Versions.leakCanaryVersion}"

    const val jacoco = "org.jacoco:org.jacoco.core:${Versions.jacocoPluginVersion}"
}