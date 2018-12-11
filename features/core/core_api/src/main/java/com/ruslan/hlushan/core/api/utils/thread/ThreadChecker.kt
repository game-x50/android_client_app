package com.ruslan.hlushan.core.api.utils.thread

interface ThreadChecker {

    val isNeededThread: Boolean
}

fun ThreadChecker.checkThread() = check(this.isNeededThread)