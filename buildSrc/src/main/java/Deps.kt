/**
 * @author Ruslan Hlushan on 11/21/18.
 */

object Deps {
    val rxJava2 = "io.reactivex.rxjava2:rxjava:${Versions.rxJava2Version}"
    val rxJava2Android = "io.reactivex.rxjava2:rxandroid:${Versions.rxJava2AndroidVersion}"

    val dagger2 = "com.google.dagger:dagger:${Versions.dagger2Version}"
    val dagger2Compiler = "com.google.dagger:dagger-compiler:${Versions.dagger2Version}"

    val kotlinxSerializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerializationVersion}"
    val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationVersion}"

    val androidCoreKtx = "androidx.core:core-ktx:${Versions.androidCoreKtxVersion}"

    val supportAnnotations = "androidx.annotation:annotation:${Versions.supportAnnotationsVersion}"
    val supportAppCompat = "androidx.appcompat:appcompat:${Versions.supportAppCompatVersion}"
    val supportDesign = "com.google.android.material:material:${Versions.supportDesignVersion}"

    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerViewVersion}"
    val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayoutVersion}"
    val raintLayout = "androidx.raintlayout:raintlayout:${Versions.raintLayoutVersion}"

    val workManager = "androidx.work:work-runtime:${Versions.workManagerVersion}"
    val workManagerRxJava2 = "androidx.work:work-rxjava2:${Versions.workManagerVersion}"

    val edgeToEdgeDecorator = "com.redmadrobot:edge-to-edge-decorator:${Versions.edgeToEdgeDecoratorVersion}"

    val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleExtensionsVersion}"

    val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBomVersion}"
    val firebaseAnalytics = "com.google.firebase:firebase-analytics"
    val firebaseAuth = "com.google.firebase:firebase-auth"
    val firebaseFirestore = "com.google.firebase:firebase-firestore"
    val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics"

    val cicerone = "com.github.terrakok:cicerone:${Versions.ciceroneVersion}"

    val okHttp3 = "com.squareup.okhttp3:okhttp:${Versions.okhttp3Version}"
    val okHttp3Logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3Version}"

    val certificateTransparencyAndroid = "com.babylon.certificatetransparency:certificatetransparency-android:${Versions.certificateTransparencyAndroidVersion}"

    val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.retrofit2Version}"
    val retrofit2KotlinxSerializationJsonConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.retrofit2KotlinxSerializationJsonConverterVersion}"
    val retrofit2RxJava2Adapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit2Version}"

    val room = "androidx.room:room-runtime:${Versions.roomVersion}"
    val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    val roomRxJava2 = "androidx.room:room-rxjava2:${Versions.roomVersion}"

    val threeTenBp = "org.threeten:threetenbp:${Versions.threeTenBpVersion}"
    val threeTenBpAndroid = "com.jakewharton.threetenabp:threetenabp:${Versions.threeTenBpAndroidVersion}"
    val threeTenBpZonedTest = "org.threeten:threetenbp:${Versions.threeTenBpZonedTestVersion}"

    val binaryPrefs = "com.github.yandextaxitech:binaryprefs:${Versions.binaryPrefsVersion}"
    val tinkAndroid = "com.google.crypto.tink:tink-android:${Versions.tinkAndroidVersion}"

    val stetho = "com.facebook.stetho:stetho:${Versions.stethoVersion}"
    val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanaryVersion}"
    val blockCanary = "com.github.markzhai:blockcanary-android:${Versions.blockCanaryVersion}"
    val tinyDancer = "com.github.brianPlummer:tinydancer:${Versions.tinyDancerVersion}"
    val takt = "jp.wasabeef:takt:${Versions.taktVersion}"
    val lynx = "com.github.pedrovgs:lynx:${Versions.lynxVersion}"
    val chucker = "com.github.ChuckerTeam.Chucker:library:${Versions.chuckerVersion}"
    val roomExplorer = "com.wajahatkarim3:roomexplorer:${Versions.roomExplorerVersion}"
    val rxDisposableWatcher = "ru.fomenkov:rx-disposable-watcher:${Versions.rxDisposableWatcherVersion}"

    val jUnit = "junit:junit:${Versions.jUnitVersion}"
}