package com.ruslan.hlushan.game.core.api.auth.dto

import java.util.regex.Pattern

data class User(
        val id: Id,
        val email: Email,
        val nickname: Nickname
) {

    @JvmInline
    value class Id(val value: String)

    @JvmInline
    value class Email private constructor(val value: String) {
        companion object {
            fun createIfValid(value: String?): Email? {
                val pattern = Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                                              + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                              + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                              + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                              + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                                              + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
                return if ((value != null)
                           && value.isNotBlank()
                           && pattern.matcher(value).matches()) {
                    Email(value)
                } else {
                    null
                }
            }
        }
    }

    @JvmInline
    value class Nickname private constructor(val value: String) {
        companion object {

            private const val MINIMAL_NICKNAME_LENGTH = 4

            fun createIfValid(value: String?): Nickname? =
                    if ((value != null)
                        && value.isNotBlank()
                        && (value.length >= MINIMAL_NICKNAME_LENGTH)) {
                        Nickname(value)
                    } else {
                        null
                    }
        }
    }

    @JvmInline
    value class Password private constructor(val value: String) {
        companion object {

            private const val MINIMAL_PASSWORD_LENGTH = 6

            fun createIfValid(value: String?): Password? =
                    if ((value != null)
                        && value.isNotBlank()
                        && (value.length >= MINIMAL_PASSWORD_LENGTH)) {
                        Password(value)
                    } else {
                        null
                    }
        }
    }
}