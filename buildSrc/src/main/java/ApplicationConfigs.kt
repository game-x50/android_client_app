/**
 * @author Ruslan Hlushan on 2019-08-28
 */

object ApplicationConfigs {
    const val minSdkVersion = 21// don't forget about proguard
    const val targetSdkVersion = 30
    const val compileSdkVersion = targetSdkVersion
    const val buildToolsVersion = "30.0.2"

    const val defaultProguardFile = "proguard-android-optimize.txt"

    const val defaultLanguageNonFullCode = "en"
    val availableLanguagesFullCodes: List<String> = listOf("en_GB", "ru_RU", "fr_FR", "es_ES", "it_IT", "pt_PT", "de_DE", "pl_PL", "be_BY")
    val applicationLanguagesNonFullCodes: List<String> = availableLanguagesFullCodes.map { fullCode -> fullCode.split("_").first() }
}