package com.ruslan.hlushan.core.ui.impl.tools

import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.impl.tools.di.getUiCoreImplStagingComponent

internal class StagingSettingsFragment : AbstractStagingSettingsFragment() {

    @UiMainThread
    override fun injectDagger2() =
            getUiCoreImplStagingComponent().inject(this)
}