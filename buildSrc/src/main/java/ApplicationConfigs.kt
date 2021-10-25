import org.gradle.api.JavaVersion

object ApplicationConfigs {
    const val minSdkVersion: Int = 21// don't forget about proguard
    const val targetSdkVersion: Int = 30
    const val compileSdkVersion: Int = targetSdkVersion
    const val buildToolsVersion: String = "30.0.2"
    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_11

    const val defaultProguardFile: String = "proguard-android-optimize.txt"

    val defaultLanguageNonFullCode: Pair<String, String> = Pair("en", "GB")
    val availableLanguagesFullCodes: List<Pair<String, String>> = listOf(
            defaultLanguageNonFullCode,
            Pair("ru", "RU"),
            Pair("fr", "FR"),
            Pair("es", "ES"),
            Pair("it", "IT"),
            Pair("pt", "PT"),
            Pair("de", "DE"),
            Pair("pl", "PL"),
            Pair("be", "BY")
    )
    val applicationLanguagesNonFullCodes: List<String> = availableLanguagesFullCodes
            .map { fullCode -> fullCode.first }
}