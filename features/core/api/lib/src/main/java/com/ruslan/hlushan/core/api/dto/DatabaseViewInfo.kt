package com.ruslan.hlushan.core.api.dto

//todo: move to separate package and
// recheck dependency graph where DatabaseViewInfo is used
data class DatabaseViewInfo(
        val clazz: Class<*>,
        val name: String
)