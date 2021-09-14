package com.ruslan.hlushan.core.api.dto

import androidx.annotation.RawRes
import java.io.File

@SuppressWarnings("LongParameterList")
data class InitAppConfig(
        val versionCode: Int,
        val versionName: String,
        val appTag: String,
        val isLogcatEnabled: Boolean,
        val fileLogsFolder: File,
        @RawRes val languagesJsonRawResId: Int,
        val defaultLanguageFullCode: String,
        val availableLanguagesFullCodes: List<String>
)