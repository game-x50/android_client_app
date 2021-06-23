import org.gradle.kotlin.dsl.extra
import kotlin.properties.ReadOnlyProperty

/**
 * @author Ruslan Hlushan on 2019-08-28
 */

object Versions {

    val gradleVersion by versionExt

    val kotlinVersion by versionExt

    val googlePluginServicesVersion by versionExt

    val crashlyticsPluginVersion by versionExt

    val detektVersion by versionExt
    val androidJarPluginVersion by versionExt

    val dependencyAnalysisPluginVersion by versionExt
    val dependencyUpdatesVersion by versionExt
    val owaspDependencyCheckVersion by versionExt
    val gradleDoctorVersion by versionExt

    val rxJava2Version by versionExt
    val rxJava2AndroidVersion by versionExt

    val dagger2Version by versionExt

    val firebaseBomVersion by versionExt

    val supportAppCompatVersion by versionExt
    val supportDesignVersion by versionExt
    val supportAnnotationsVersion by versionExt
    val swipeRefreshLayoutVersion by versionExt
    val recyclerViewVersion by versionExt

    val raintLayoutVersion by versionExt

    val androidCoreKtxVersion by versionExt

    val workManagerVersion by versionExt

    val edgeToEdgeDecoratorVersion by versionExt

    val lifecycleExtensionsVersion by versionExt

    val ciceroneVersion by versionExt

    val retrofit2Version by versionExt
    val okhttp3Version by versionExt

    val certificateTransparencyAndroidVersion by versionExt

    val kotlinxSerializationVersion by versionExt
    val retrofit2KotlinxSerializationJsonConverterVersion by versionExt

    val roomVersion by versionExt

    val threeTenBpVersion by versionExt
    val threeTenBpAndroidVersion by versionExt
    val threeTenBpZonedTestVersion by versionExt

    val binaryPrefsVersion by versionExt
    val tinkAndroidVersion by versionExt

    val stethoVersion by versionExt
    val leakCanaryVersion by versionExt
    val blockCanaryVersion by versionExt
    val tinyDancerVersion by versionExt
    val taktVersion by versionExt
    val lynxVersion by versionExt
    val chuckerVersion by versionExt
    val roomExplorerVersion by versionExt
    val rxDisposableWatcherVersion by versionExt

    val jacocoPluginVersion by versionExt
    val jUnitVersion by versionExt
}

private val versions: Map<String, String>
    get() = (SomeUnknownConfig.rootProject.extra["versions"] as Map<String, String>)

//https://vk.com/video-147415323_456239467?t=1h47m39s
private val versionExt: ReadOnlyProperty<Any, String> =
        ReadOnlyProperty { thisRef, property -> versions[property.name]!! }