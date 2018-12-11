/**
 * @author Ruslan Hlushan on 2019-08-28
 */

object Plugins {
    const val kotlin = "kotlin"
    const val kotlinAndroid = "kotlin-android"

    const val androidLibrary = "com.android.library"
    const val androidApp = "com.android.application"

    const val androidJar = "com.stepango.androidjar"
    const val detekt = "io.gitlab.arturbosch.detekt"
    const val crashlytics = "com.google.firebase.crashlytics"
    const val googleServices = "com.google.gms.google-services"
    const val dependencyAnalysis = "com.autonomousapps.dependency-analysis"
    const val dependencyUpdates = "com.github.ben-manes.versions"
    const val owaspDependencyCheck = "org.owasp.dependencycheck"
    const val gradleDoctor = "com.osacky.doctor"
    const val kotlinxSerilization = "kotlinx-serialization"
    const val kotlinParcelize = "kotlin-parcelize"
    const val leakCanaryDeobfuscation = "com.squareup.leakcanary.deobfuscation"

    const val jacoco = "jacoco"

    const val kotlinxSerilizationPluginPath = "plugin.serialization"
    const val kotlinParcelizePluginPath = "org.jetbrains.kotlin.plugin.parcelize"
    const val detektFormattingPlugin = "${detekt}:detekt-formatting:${Versions.detektVersion}"
}