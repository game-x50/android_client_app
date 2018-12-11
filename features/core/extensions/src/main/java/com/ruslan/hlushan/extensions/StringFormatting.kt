package com.ruslan.hlushan.extensions

@SuppressWarnings("ImplicitDefaultLocale")
fun Number.formatWithLeadingZerosString(minStringSize: Int): String =
        String.format("%0${minStringSize}d", this)