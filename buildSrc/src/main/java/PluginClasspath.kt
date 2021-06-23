/**
 * @author Ruslan Hlushan on 2019-08-28
 */

object PluginClasspath {

    val androidGradle = "com.android.tools.build:gradle:${Versions.gradleVersion}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val kotlinxSerilization = "plugin.serialization"
    val googleServices = "com.google.gms:google-services:${Versions.googlePluginServicesVersion}"
    val crashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsPluginVersion}"
    val leakCanaryDeobfuscation = "com.squareup.leakcanary:leakcanary-deobfuscation-gradle-plugin:${Versions.leakCanaryVersion}"

    val jacoco = "org.jacoco:org.jacoco.core:${Versions.jacocoPluginVersion}"
}