package com.ruslan.hlushan.core.ui.impl.tools

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.CallSuper
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.android.extensions.applyDrawableOverlay
import com.ruslan.hlushan.android.extensions.clearOverlay
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.android.extensions.toPx
import com.ruslan.hlushan.core.api.tools.BlockCanaryTool
import com.ruslan.hlushan.core.api.tools.ChuckTool
import com.ruslan.hlushan.core.api.tools.DatabaseViewerTool
import com.ruslan.hlushan.core.api.tools.LeakCanaryTool
import com.ruslan.hlushan.core.api.tools.LynxTool
import com.ruslan.hlushan.core.api.tools.RxDisposableWatcherTool
import com.ruslan.hlushan.core.api.tools.TaktTool
import com.ruslan.hlushan.core.api.tools.TinyDancerTool
import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.logger.api.FileLogger
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsDeveloperSettingsBinding
import com.ruslan.hlushan.core.ui.impl.tools.file.FileLogsActivity
import com.ruslan.hlushan.core.ui.impl.tools.utils.GridDrawable
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding
import com.ruslan.hlushan.third_party.androidx.room.utils.DatabaseViewInfo
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DEFAULT_GRID_DISTANCE_DP: Int = 16

@Suppress("MaxLineLength")
abstract class AbstractStagingSettingsFragment
@ContentView
constructor(
        @LayoutRes layoutResId: Int
) : BaseFragment(layoutResId = layoutResId) {

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

    protected abstract val stagingSettingsBinding: CoreUiImplStagingToolsDeveloperSettingsBinding?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        needRecreateOverlay = (savedInstanceState != null)

        view.addSystemPadding(top = true, bottom = true)

        setUpViews()
        setUpViewListeners()
    }

    override fun onResume() {
        super.onResume()

        stagingSettingsBinding?.fragmentDevSettingsTinyDancerSwitch?.isChecked = tinyDancerTool.show
        stagingSettingsBinding?.fragmentDevSettingsTaktSwitch?.isChecked = taktTool.show
        stagingSettingsBinding?.fragmentDevSettingsLeakCanarySwitch?.isChecked = leakCanaryTool.enabled
        stagingSettingsBinding?.fragmentDevSettingsFileLogsEnabledSwitch?.isChecked = fileLogger.enabled

        recreateOverlayIfNeeded()
    }

    @UiMainThread
    private fun setUpViews() {
        stagingSettingsBinding?.fragmentDevSettingsVersionCodeTextView?.text = initAppConfig.versionCode.toString()
        stagingSettingsBinding?.fragmentDevSettingsVersionNameTextView?.text = initAppConfig.versionName

        setUpSpinnerDatabase()
    }

    @UiMainThread
    @CallSuper
    protected open fun setUpViewListeners() {
        stagingSettingsBinding?.fragmentDevSettingsTinyDancerSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            tinyDancerTool.show = isChecked
        }
        stagingSettingsBinding?.fragmentDevSettingsTaktSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            taktTool.show = isChecked
        }
        stagingSettingsBinding?.fragmentDevSettingsLeakCanarySwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            leakCanaryTool.enabled = isChecked
        }
        stagingSettingsBinding?.fragmentDevSettingsFileLogsEnabledSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            fileLogger.enabled = isChecked
        }
        stagingSettingsBinding?.fragmentDevSettingsDrawGridSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            setUpDrawGrid(isChecked)
        }

        stagingSettingsBinding?.fragmentDevSettingsStartBlockCanaryBtn?.setThrottledOnClickListener { blockCanaryTool.start() }
        stagingSettingsBinding?.fragmentDevSettingsStopBlockCanaryBtn?.setThrottledOnClickListener { blockCanaryTool.stop() }

        stagingSettingsBinding?.fragmentDevSettingsOpenChuckBtn?.setThrottledOnClickListener { chuckTool.openScreen() }
        stagingSettingsBinding?.fragmentDevSettingsRxDisposableWatcherReportBtn?.setThrottledOnClickListener {
            rxDisposableWatcherTool.showReport()
        }
        stagingSettingsBinding?.fragmentDevSettingsOpenLeakCanaryBtn?.setThrottledOnClickListener { leakCanaryTool.openScreen() }
        stagingSettingsBinding?.fragmentDevSettingsOpenBlockCanaryBtn?.setThrottledOnClickListener { blockCanaryTool.openScreen() }
        stagingSettingsBinding?.fragmentDevSettingsOpenLynxBtn?.setThrottledOnClickListener { lynxTool.openScreen() }
        stagingSettingsBinding?.fragmentDevSettingsOpenFileLogsBtn?.setThrottledOnClickListener {
            ifNotNull(activity) { nonNullActivity ->
                nonNullActivity.startActivity(FileLogsActivity.newIntent(nonNullActivity))
            }
        }

        stagingSettingsBinding?.fragmentDevSettingsOpenDatabaseBtn?.setThrottledOnClickListener {
            val database = (stagingSettingsBinding?.fragmentDevSettingsDatabaseSpinner?.selectedItem as DatabaseViewInfo)
            databaseViewerTool.showDatabase(database)
        }
    }

    @UiMainThread
    private fun setUpSpinnerDatabase() {
        stagingSettingsBinding?.fragmentDevSettingsDatabaseSpinner?.adapter = ArrayAdapter<DatabaseViewInfo>(
                requireContext(),
                R.layout.core_ui_impl_staging_tools_developer_settings_spinner_item,
                databases
        )
    }

    @UiMainThread
    private fun setUpDrawGrid(draw: Boolean) {
        if (isResumed) {
            if (draw) {
                val distance = (stagingSettingsBinding?.fragmentDevSettingsDrawGridDistanceInput?.text?.toString()?.toIntOrNull()
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
                ifNotNull(stagingSettingsBinding?.fragmentDevSettingsDrawGridSwitch?.isChecked) { isChecked ->
                    setUpDrawGrid(isChecked)
                }
                needRecreateOverlay = false
            }
        }
    }
}