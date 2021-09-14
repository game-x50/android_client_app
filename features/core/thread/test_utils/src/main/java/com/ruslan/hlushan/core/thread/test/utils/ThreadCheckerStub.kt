package com.ruslan.hlushan.core.thread.test.utils

import com.ruslan.hlushan.core.thread.ThreadChecker

class ThreadCheckerStub(
        override val isNeededThread: Boolean
) : ThreadChecker