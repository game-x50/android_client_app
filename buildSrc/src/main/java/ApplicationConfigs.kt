import org.gradle.api.JavaVersion

object ApplicationConfigs {
    const val minSdkVersion: Int = 21// don't forget about proguard
    const val targetSdkVersion: Int = 30
    const val compileSdkVersion: Int = targetSdkVersion
    const val buildToolsVersion: String = "30.0.2"
    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_11

    const val defaultProguardFile: String = "proguard-android-optimize.txt"

    const val defaultLanguageNonFullCode: String = "en"
    val availableLanguagesFullCodes: List<String> = listOf("en_GB", "ru_RU", "fr_FR", "es_ES", "it_IT", "pt_PT", "de_DE", "pl_PL", "be_BY")
    val applicationLanguagesNonFullCodes: List<String> = availableLanguagesFullCodes.map { fullCode -> fullCode.split("_").first() }
}