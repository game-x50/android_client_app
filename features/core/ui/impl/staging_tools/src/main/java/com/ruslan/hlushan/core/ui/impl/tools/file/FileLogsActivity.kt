package com.ruslan.hlushan.core.ui.impl.tools.file

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ruslan.hlushan.android.core.api.di.createExternalReportsFileWithReadPermissionsForOtherApps
import com.ruslan.hlushan.android.core.api.di.createOpenFileWithReadPermissionForOtherApps
import com.ruslan.hlushan.android.core.api.di.getUriForFile
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.android.extensions.showSystemMessage
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.command.extensions.handleCommandQueue
import com.ruslan.hlushan.core.ui.activity.BaseActivity
import com.ruslan.hlushan.core.ui.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.impl.tools.R
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsFileLogsScreenBinding
import com.ruslan.hlushan.core.ui.impl.tools.di.getUiCoreImplStagingHelpersComponent
import com.ruslan.hlushan.core.ui.pagination.view.setUpPagination
import com.ruslan.hlushan.core.ui.pagination.viewmodel.PaginationState
import com.ruslan.hlushan.core.ui.recycler.adapter.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.recycler.adapter.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.viewmodel.extensions.bindBaseViewModel
import com.ruslan.hlushan.extensions.exhaustive
import java.io.File

internal class FileLogsActivity : BaseActivity() {

    private val logsAdapter = DelegatesRecyclerAdapter(LogsAdapterDelegate())

    private val binding by bindViewBinding(
            CoreUiImplStagingToolsFileLogsScreenBinding::bind,
            R.id.v_root_activity_file_logs
    )

    private val viewModel: FileLogsViewModel by bindBaseViewModel {
        this.getUiCoreImplStagingHelpersComponent().fileLogsViewModelFactory().create()
    }

    @UiMainThread
    override fun initDagger2() = this.getUiCoreImplStagingHelpersComponent().inject(this)

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver { binding?.activityFileLogsList })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setUpPagination(
                recyclerAdapter = logsAdapter,
                paginationViewModel = viewModel,
                recyclerView = binding?.activityFileLogsList,
                swipeRefreshLayout = binding?.activityFileLogsSwipeRefresh
        )

        initViewListeners()

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    override fun initContentView() =
            setContentView(R.layout.core_ui_impl_staging_tools_file_logs_screen)

    @UiMainThread
    private fun initViewListeners() {
        binding?.activityFileLogsCopyLogsAsSingleExternalStorageFileBtn?.setThrottledOnClickListener {
            validateFileNameAndCopyAllExistingLogsToSingleExternalStorageFile()
        }
        binding?.activityFileLogsDeleteLogFilesBtn?.setThrottledOnClickListener { viewModel.deleteLogFiles() }
        binding?.activityFileLogsBigLogBtn?.setThrottledOnClickListener { viewModel.bigLog() }
    }

    @UiMainThread
    private fun handleCommand(command: FileLogsViewModel.Command) =
            when (command) {
                is FileLogsViewModel.Command.ShowSimpleProgress -> showSimpleProgress(show = command.show)
                is FileLogsViewModel.Command.ShowMessage        -> showSystemMessage(text = command.message)
                is FileLogsViewModel.Command.SetState           -> setState(command)
                is FileLogsViewModel.Command.OpenFile           -> openFile(file = command.file)
            }

    @UiMainThread
    private fun setState(command: FileLogsViewModel.Command.SetState) {
        logsAdapter.submitList(command.logs)

        @Suppress("MaxLineLength")
        binding?.activityFileLogsSwipeRefresh?.isRefreshing = (command.additional is PaginationState.Additional.Loading)

        when (command.additional) {
            is PaginationState.Additional.WaitingForLoadMore -> {
                //TODO
            }
            is PaginationState.Additional.Error              -> {
                //TODO
            }
            is PaginationState.Additional.Loading,
            null                                             -> Unit
        }.exhaustive
    }

    @UiMainThread
    private fun validateFileNameAndCopyAllExistingLogsToSingleExternalStorageFile() {
        val fileName: String = binding?.activityFileLogsSingleFileLogsName?.text.toString()
        val fileNameValid: Boolean = fileName.isNotBlank()

        if (fileNameValid) {
            val destination = this.createExternalReportsFileWithReadPermissionsForOtherApps(
                    fileName = "$fileName.txt"
            )
            viewModel.copyAllExistingLogsToSingleExternalStorageFile(destination = destination.file)
        } else {
            showSystemMessage(text = "Enter valid file name")
        }
    }

    @UiMainThread
    private fun openFile(file: File) {
        val fileUri = this.getUriForFile(file)
        val chooserIntent = createOpenFileWithReadPermissionForOtherApps(
                contentResolver = this.contentResolver,
                uri = fileUri
        )
        this.startActivity(chooserIntent)
    }

    companion object {
        fun newIntent(context: Context): Intent =
                Intent(context, FileLogsActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}