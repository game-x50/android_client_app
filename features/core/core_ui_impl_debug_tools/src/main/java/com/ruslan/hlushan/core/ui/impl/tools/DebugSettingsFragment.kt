package com.ruslan.hlushan.core.ui.impl.tools

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.impl.tools.di.getUiCoreImplDebugComponent

internal class DebugSettingsFragment : AbstractStagingSettingsFragment() {

    @UiMainThread
    override fun injectDagger2() =
            getUiCoreImplDebugComponent().inject(this)
}