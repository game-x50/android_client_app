package com.ruslan.hlushan.core.language.api

import com.ruslan.hlushan.core.api.dto.LangFullCode

data class Language(
        val code: LangFullCode,
        val name: String,
        val imageUrl: String?,
        val alphabet: String
)