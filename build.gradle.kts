buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(PluginClasspath.androidGradle)
        classpath(PluginClasspath.googleServices)
        classpath(PluginClasspath.crashlytics)
        classpath(PluginClasspath.kotlin)
        classpath(PluginClasspath.leakCanaryDeobfuscation)
        classpath(PluginClasspath.jacoco)
    }
}

plugins {
    id(Plugins.androidJar).version(Versions.androidJarPluginVersion)

    id(Plugins.detekt).version(Versions.detektVersion)
    id(Plugins.dependencyAnalysis).version(Versions.dependencyAnalysisPluginVersion)
    id(Plugins.owaspDependencyCheck).version(Versions.owaspDependencyCheckVersion)
    id(Plugins.gradleDoctor).version(Versions.gradleDoctorVersion)
    id(Plugins.dependencyUpdates).version(Versions.dependencyUpdatesVersion)

    kotlin(Plugins.kotlinxSerilizationPluginPath).version(Versions.kotlinVersion)
    id(Plugins.kotlinParcelizePluginPath).version(Versions.kotlinVersion)
}
extra["androidJar"] = androidjar.find(ApplicationConfigs.targetSdkVersion)

val gradleSupportFolderName: String = "gradle_support"
fun getGradleSupportFolder(project: Project): String = "${project.rootProject.projectDir.path}/${gradleSupportFolderName}/"

allprojects {
    val rootProjectPath = project.rootProject.projectDir.path

    val gradleSupportFolder = getGradleSupportFolder(this)

    extra[GradleExtraArgs.lintersConfigFolder] = "$rootProjectPath/linters/"
    extra[GradleExtraArgs.localizationFolder] = "$rootProjectPath/localization"
    extra[GradleExtraArgs.proguardConfigsFolder] = "$rootProjectPath/proguard_configs"

    extra[GradleExtraArgs.baseAndroid] = "${gradleSupportFolder}base_android.gradle"

    extra[GradleExtraArgs.baseKotlinLibrary] = "${gradleSupportFolder}base_kotlin_library.gradle"
    extra[GradleExtraArgs.baseAndroidLibrary] = "${gradleSupportFolder}base_android_library.gradle"
    extra[GradleExtraArgs.baseAndroidApp] = "${gradleSupportFolder}base_android_app.gradle"

    extra[GradleExtraArgs.androidMinifyDisabled] = "${gradleSupportFolder}android_minify_disabled.gradle"
    extra[GradleExtraArgs.kapt] = "${gradleSupportFolder}kapt.gradle"
    extra[GradleExtraArgs.apkSigning] = "${gradleSupportFolder}apk-signing.gradle"
    extra[GradleExtraArgs.androidSingleBuildVariant] = "${gradleSupportFolder}android_single_build_variant.gradle"
    extra[GradleExtraArgs.androidAllBuildVariants] = "${gradleSupportFolder}android_all_build_variants.gradle"
    extra[GradleExtraArgs.applicationBuildVariants] = "${gradleSupportFolder}application_build_variants.gradle"
    extra[GradleExtraArgs.androidLinters] = "${gradleSupportFolder}android_linters.gradle"
    extra[GradleExtraArgs.dagger2Kapt] = "${gradleSupportFolder}dagger2_kapt.gradle"
    extra[GradleExtraArgs.room] = "${gradleSupportFolder}room.gradle"
    extra[GradleExtraArgs.viewBinding] = "${gradleSupportFolder}android_view_binding.gradle"
    extra[GradleExtraArgs.kotlinxSerilization] = "${gradleSupportFolder}kotlinx_serilization.gradle"
    extra[GradleExtraArgs.applicationLeakCanaryTool] = "${gradleSupportFolder}application_leak_canary_tool.gradle"
    extra[GradleExtraArgs.projectPropertiesRead] = "${gradleSupportFolder}project_properties_read.gradle"

    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
        maven(url = "https://maven.google.com")
    }

    apply(plugin = Plugins.owaspDependencyCheck)
}

subprojects {
    val gradleSupportFolder = getGradleSupportFolder(this)

    apply(from = "${gradleSupportFolder}jacoco_subprojects_configuration.gradle")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

apply(from = "${project.rootProject.projectDir.path}/${gradleSupportFolderName}/linters.gradle")

tasks.named(
        "dependencyUpdates",
        com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java
)
        .configure {

            rejectVersionIf {
                (!isStableVersionName(candidate.version) && isStableVersionName(currentVersion))
            }

            outputFormatter = "plain"
            revision = "release"
        }

//TODO: #PROGUARD_TAG: https://proandroiddev.com/kotlin-cleaning-java-bytecode-before-release-9567d4c63911
//tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class)
//        .all {
//            kotlinOptions {
//                freeCompilerArgs += listOf(
//                        "-Xno-call-assertions",
//                        "-Xno-receiver-assertions",
//                        "-Xno-param-assertions"
//                )
//            }
//        }

fun isStableVersionName(version: String): Boolean {
    val isStableKeyword = arrayOf("RELEASE", "FINAL", "GA")
            .any { stableKeyword -> version.contains(stableKeyword, ignoreCase = true) }

    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = (isStableKeyword || regex.matches(version))

    return isStable
}
