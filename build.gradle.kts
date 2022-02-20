buildscript {

    repositories {
        google()
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(PluginClasspath.androidGradle)
        classpath(PluginClasspath.googleServices)
        classpath(PluginClasspath.crashlytics)
        classpath(PluginClasspath.kotlin)
        classpath(PluginClasspath.leakCanaryDeobfuscation)
        classpath(PluginClasspath.jacoco)
        classpath(PluginClasspath.licensee)
    }
}

plugins {
    id(Plugins.androidJar).version(Versions.androidJarPluginVersion)

    id(Plugins.detekt).version(Versions.detektVersion)
    id(Plugins.dependencyAnalysis).version(Versions.dependencyAnalysisPluginVersion)
    id(Plugins.owaspDependencyCheck).version(Versions.owaspDependencyCheckVersion)
    id(Plugins.gradleDoctor).version(Versions.gradleDoctorVersion)
    id(Plugins.dependencyUpdates).version(Versions.dependencyUpdatesVersion)
    id(Plugins.sonarqube).version(Versions.sonarqubePluginVersion)
    id(Plugins.diktat).version(Versions.diktatVersion)

    kotlin(Plugins.kotlinxSerilizationPluginPath).version(Versions.kotlinVersion)
}
extra["androidJar"] = androidjar.find(ApplicationConfigs.targetSdk)

allprojects {

    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://maven.google.com")
        jcenter()
    }

    apply(plugin = Plugins.owaspDependencyCheck)
}

subprojects {
    val gradleSupportFolder = GradleExtraArgs.getGradleSupportFolder(this)

    apply(from = "${gradleSupportFolder}jacoco_subprojects_configuration.gradle")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

apply(from = "${GradleExtraArgs.getGradleSupportFolder(project)}linters.gradle")

apply(plugin = Plugins.diktat)
diktat {
    inputs {
        include("**/src/**/*.kt")
    }
    diktatConfigFile = file("${GradleExtraArgs.getLintersConfigFolder(rootProject)}diktat-analysis.yml")
    output = "${GradleExtraArgs.getReportsFolder(rootProject)}diktat.txt"
}

apply(from = "${GradleExtraArgs.getGradleSupportFolder(project)}sonarqube.gradle")

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