package com.ruslan.hlushan.core.thread

interface ThreadChecker {

    val isNeededThread: Boolean
}

fun ThreadChecker.checkThread() = check(this.isNeededThread)