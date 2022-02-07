package com.ruslan.hlushan.core.ui.impl.tools

import com.ruslan.hlushan.core.logger.api.LogcatLogger
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplDebugToolsDeveloperSettingsBinding
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplDebugToolsDeveloperSettingsScreenBinding
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsDeveloperSettingsBinding
import com.ruslan.hlushan.core.ui.impl.tools.di.getUiCoreImplDebugComponent
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding
import javax.inject.Inject

@Suppress("MaxLineLength")
internal class DebugSettingsFragment : AbstractStagingSettingsFragment(
        layoutResId = R.layout.core_ui_impl_debug_tools_developer_settings_screen
) {

    private val screenBinding by bindViewBinding(CoreUiImplDebugToolsDeveloperSettingsScreenBinding::bind)

    @Inject lateinit var logcatLogger: LogcatLogger

    override val stagingSettingsBinding: CoreUiImplStagingToolsDeveloperSettingsBinding?
        get() = screenBinding?.stagingSettings

    private val debugSettingsBinding: CoreUiImplDebugToolsDeveloperSettingsBinding?
        get() = screenBinding?.debugSettings

    @UiMainThread
    override fun injectDagger2() =
            getUiCoreImplDebugComponent().inject(this)

    override fun onResume() {
        super.onResume()

        debugSettingsBinding?.fragmentDevSettingsLogcatLogsEnabledSwitch?.isChecked = logcatLogger.enabled
    }

    override fun setUpViewListeners() {
        super.setUpViewListeners()

        debugSettingsBinding?.fragmentDevSettingsLogcatLogsEnabledSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            logcatLogger.enabled = isChecked
        }
    }
}