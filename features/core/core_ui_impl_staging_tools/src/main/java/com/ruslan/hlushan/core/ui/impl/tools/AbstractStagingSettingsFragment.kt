package com.ruslan.hlushan.core.ui.impl.tools

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.ruslan.hlushan.android.extensions.addSystemPadding
import com.ruslan.hlushan.android.extensions.applyDrawableOverlay
import com.ruslan.hlushan.android.extensions.clearOverlay
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.android.extensions.toPx
import com.ruslan.hlushan.core.api.dto.DatabaseViewInfo
import com.ruslan.hlushan.core.api.log.FileLogger
import com.ruslan.hlushan.core.api.tools.BlockCanaryTool
import com.ruslan.hlushan.core.api.tools.ChuckTool
import com.ruslan.hlushan.core.api.tools.DatabaseViewerTool
import com.ruslan.hlushan.core.api.tools.LeakCanaryTool
import com.ruslan.hlushan.core.api.tools.LynxTool
import com.ruslan.hlushan.core.api.tools.RxDisposableWatcherTool
import com.ruslan.hlushan.core.api.tools.TaktTool
import com.ruslan.hlushan.core.api.tools.TinyDancerTool
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.api.view.GridDrawable
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsDeveloperSettingsScreenBinding
import com.ruslan.hlushan.core.ui.impl.tools.file.FileLogsActivity
import com.ruslan.hlushan.extensions.ifNotNull
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DEFAULT_GRID_DISTANCE_DP: Int = 16

abstract class AbstractStagingSettingsFragment : BaseFragment(
        layoutResId = R.layout.core_ui_impl_staging_tools_developer_settings_screen
) {

    private val binding by bindViewBinding(CoreUiImplStagingToolsDeveloperSettingsScreenBinding::bind)

    @Inject lateinit var initAppConfig: InitAppConfig
    @Inject lateinit var leakCanaryTool: LeakCanaryTool
    @Inject lateinit var blockCanaryTool: BlockCanaryTool
    @Inject lateinit var tinyDancerTool: TinyDancerTool
    @Inject lateinit var taktTool: TaktTool
    @Inject lateinit var lynxTool: LynxTool
    @Inject lateinit var databaseViewerTool: DatabaseViewerTool
    @Inject lateinit var chuckTool: ChuckTool
    @Inject lateinit var rxDisposableWatcherTool: RxDisposableWatcherTool

    @Inject lateinit var databases: List<DatabaseViewInfo>

    @Inject lateinit var fileLogger: FileLogger

    private var needRecreateOverlay: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        needRecreateOverlay = (savedInstanceState != null)

        view.addSystemPadding(top = true, bottom = true)

        setUpViews()
        setUpViewListeners()
    }

    override fun onResume() {
        super.onResume()

        binding?.fragmentDevSettingsTinyDancerSwitch?.isChecked = tinyDancerTool.show
        binding?.fragmentDevSettingsTaktSwitch?.isChecked = taktTool.show
        binding?.fragmentDevSettingsLeakCanarySwitch?.isChecked = leakCanaryTool.enabled
        binding?.fragmentDevSettingsFileLogsEnabledSwitch?.isChecked = fileLogger.enabled

        recreateOverlayIfNeeded()
    }

    @UiMainThread
    private fun setUpViews() {
        binding?.fragmentDevSettingsVersionCodeTextView?.text = initAppConfig.versionCode.toString()
        binding?.fragmentDevSettingsVersionNameTextView?.text = initAppConfig.versionName

        setUpSpinnerDatabase()
    }

    @UiMainThread
    private fun setUpViewListeners() {
        binding?.fragmentDevSettingsTinyDancerSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            tinyDancerTool.show = isChecked
        }
        binding?.fragmentDevSettingsTaktSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            taktTool.show = isChecked
        }
        binding?.fragmentDevSettingsLeakCanarySwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            leakCanaryTool.enabled = isChecked
        }
        binding?.fragmentDevSettingsFileLogsEnabledSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            fileLogger.enabled = isChecked
        }
        binding?.fragmentDevSettingsDrawGridSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            setUpDrawGrid(isChecked)
        }

        binding?.fragmentDevSettingsStartBlockCanaryBtn?.setThrottledOnClickListener { blockCanaryTool.start() }
        binding?.fragmentDevSettingsStopBlockCanaryBtn?.setThrottledOnClickListener { blockCanaryTool.stop() }

        binding?.fragmentDevSettingsOpenChuckBtn?.setThrottledOnClickListener { chuckTool.openScreen() }
        binding?.fragmentDevSettingsRxDisposableWatcherReportBtn?.setThrottledOnClickListener {
            rxDisposableWatcherTool.showReport()
        }
        binding?.fragmentDevSettingsOpenLeakCanaryBtn?.setThrottledOnClickListener { leakCanaryTool.openScreen() }
        binding?.fragmentDevSettingsOpenBlockCanaryBtn?.setThrottledOnClickListener { blockCanaryTool.openScreen() }
        binding?.fragmentDevSettingsOpenLynxBtn?.setThrottledOnClickListener { lynxTool.openScreen() }
        binding?.fragmentDevSettingsOpenFileLogsBtn?.setThrottledOnClickListener {
            ifNotNull(activity) { nonNullActivity ->
                nonNullActivity.startActivity(FileLogsActivity.newIntent(nonNullActivity))
            }
        }

        binding?.fragmentDevSettingsOpenDatabaseBtn?.setThrottledOnClickListener {
            val database = (binding?.fragmentDevSettingsDatabaseSpinner?.selectedItem as DatabaseViewInfo)
            databaseViewerTool.showDatabase(database)
        }
    }

    @UiMainThread
    private fun setUpSpinnerDatabase() {
        binding?.fragmentDevSettingsDatabaseSpinner?.adapter = ArrayAdapter<DatabaseViewInfo>(
                requireContext(),
                R.layout.core_ui_impl_staging_tools_developer_settings_spinner_item,
                databases
        )
    }

    @UiMainThread
    private fun setUpDrawGrid(draw: Boolean) {
        if (isResumed) {
            if (draw) {
                val distance = (binding?.fragmentDevSettingsDrawGridDistanceInput?.text?.toString()?.toIntOrNull()
                                ?: DEFAULT_GRID_DISTANCE_DP)
                val distancePx = distance.toFloat().toPx().toInt()

                activity?.applyDrawableOverlay(GridDrawable(
                        linesColor = Color.BLACK,
                        distancePx = distancePx
                ))
            } else {
                activity?.clearOverlay()
            }
        }
    }

    @UiMainThread
    private fun recreateOverlayIfNeeded() {
        @Suppress("MagicNumber")
        viewsHandler.postDelayed(100, TimeUnit.MILLISECONDS) {
            if (needRecreateOverlay) {
                ifNotNull(binding?.fragmentDevSettingsDrawGridSwitch?.isChecked) { isChecked ->
                    setUpDrawGrid(isChecked)
                }
                needRecreateOverlay = false
            }
        }
    }
}