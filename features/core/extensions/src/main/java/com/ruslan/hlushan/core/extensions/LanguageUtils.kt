package com.ruslan.hlushan.core.extensions

fun defIfEmpty(lang: String?, defaultLanguageFullCode: String): String =
        (lang?.nullIfBlank() ?: defaultLanguageFullCode)

fun bottomLineToUpperLineCode(codeWithBottomLine: String?, defaultLanguageFullCode: String): String {
    val codeWithBottomLineVar = defIfEmpty(codeWithBottomLine, defaultLanguageFullCode)
    return codeWithBottomLineVar.replace("_", "-")
}

fun fullLangCodeToNotFull(fullCode: String?, defaultLanguageFullCode: String): String {
    val fullCodeVar = defIfEmpty(fullCode, defaultLanguageFullCode)
    return fullCodeVar.split("_".toRegex())[0]
}