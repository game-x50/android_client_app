package com.ruslan.hlushan.core.api.test.utils.thread

import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker

class ThreadCheckerStub(
        override val isNeededThread: Boolean
) : ThreadChecker