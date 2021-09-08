package com.ruslan.hlushan.core.ui.viewmodel.test.utils

import com.ruslan.hlushan.core.ui.viewmodel.BaseViewModel
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestBaseViewModelCleanUpRule(
        private val viewModel: () -> BaseViewModel
) : TestWatcher() {

    override fun finished(description: Description?) {
        super.finished(description)
        @Suppress("VisibleForTests")
        viewModel().cleanUpAll()
    }
}