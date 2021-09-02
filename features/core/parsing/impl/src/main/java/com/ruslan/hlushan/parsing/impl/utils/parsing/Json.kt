package com.ruslan.hlushan.parsing.impl.utils.parsing

import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json

val AppJson: StringFormat = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}