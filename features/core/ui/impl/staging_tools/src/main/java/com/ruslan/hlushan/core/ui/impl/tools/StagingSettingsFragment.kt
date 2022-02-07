package com.ruslan.hlushan.core.ui.impl.tools

import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsDeveloperSettingsBinding
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsDeveloperSettingsScreenBinding
import com.ruslan.hlushan.core.ui.impl.tools.di.getUiCoreImplStagingComponent
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding

internal class StagingSettingsFragment : AbstractStagingSettingsFragment(
        layoutResId = R.layout.core_ui_impl_staging_tools_developer_settings_screen
) {

    private val screenBinding by bindViewBinding(CoreUiImplStagingToolsDeveloperSettingsScreenBinding::bind)

    override val stagingSettingsBinding: CoreUiImplStagingToolsDeveloperSettingsBinding?
        get() = screenBinding?.stagingSettings

    @UiMainThread
    override fun injectDagger2() =
            getUiCoreImplStagingComponent().inject(this)
}