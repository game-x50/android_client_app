package com.ruslan.hlushan.core.ui.impl.tools.file

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.ruslan.hlushan.android.extensions.permissions.PermissionResult
import com.ruslan.hlushan.android.extensions.permissions.PermissionResultListener
import com.ruslan.hlushan.android.extensions.permissions.SeparatedPermissions
import com.ruslan.hlushan.android.extensions.permissions.askPermissions
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.android.extensions.showSystemMessage
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.core.ui.api.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.api.presentation.command.handleCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.presentation.view.activity.BaseActivity
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.setUpPagination
import com.ruslan.hlushan.core.ui.api.recycler.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.impl.tools.R
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsFileLogsScreenBinding
import com.ruslan.hlushan.core.ui.impl.tools.di.getUiCoreImplStagingHelpersComponent
import com.ruslan.hlushan.extensions.exhaustive
import java.io.File

internal class FileLogsActivity : BaseActivity(), PermissionResultListener {

    @SuppressWarnings("ClassOrdering")
    companion object {

        private const val PERMISSIONS_REQUEST_CODE = 1123

        fun newIntent(context: Context): Intent =
                Intent(context, FileLogsActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private val logsAdapter = DelegatesRecyclerAdapter(LogsAdapterDelegate())

    private val binding by bindViewBinding(CoreUiImplStagingToolsFileLogsScreenBinding::bind, R.id.v_root_activity_file_logs)

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
    override fun onPermissionResult(permissionResult: PermissionResult) {
        if (permissionResult.requestCode == PERMISSIONS_REQUEST_CODE) {
            when (permissionResult) {
                is PermissionResult.ShowRationale -> showSystemMessage(text = "Grant Permission")
                is PermissionResult.Response      -> handlePermissionResultResponse(permissionResult)
            }.exhaustive
        }
    }

    @UiMainThread
    private fun handlePermissionResultResponse(permissionResult: PermissionResult.Response) =
            when (permissionResult.permissions) {
                is SeparatedPermissions.AllGranted                              -> {
                    validateFileNameAndCopyAllExistingLogsToSingleExternalStorageFileOnPermissionGranted()
                }
                is SeparatedPermissions.AtLeastOneTemporallyDenied,
                is SeparatedPermissions.DeniedJustPermanentlyAndMaybeAreGranted -> {
                    showSystemMessage(text = "Grant All Permissions")
                }
            }

    @UiMainThread
    private fun initViewListeners() {
        binding?.activityFileLogsCopyLogsAsSingleExternalStorageFileBtn?.setThrottledOnClickListener {
            this.askPermissions(
                    requestCode = PERMISSIONS_REQUEST_CODE,
                    permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
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
            }

    @UiMainThread
    private fun setState(command: FileLogsViewModel.Command.SetState) {
        logsAdapter.submitList(command.logs)

        binding?.activityFileLogsSwipeRefresh?.isRefreshing = (command.additional is PaginationState.Additional.Loading)

        when (command.additional) {
            is PaginationState.Additional.WaitingForLoadMore -> {
                //TODO
            }
            is PaginationState.Additional.Error -> {
                //TODO
            }
            is PaginationState.Additional.Loading,
            null                                -> Unit
        }.exhaustive
    }

    @UiMainThread
    private fun validateFileNameAndCopyAllExistingLogsToSingleExternalStorageFileOnPermissionGranted() {
        val fileName: String = binding?.activityFileLogsSingleFileLogsName?.text.toString()
        val fileNameValid: Boolean = fileName.isNotBlank()

        if (fileNameValid) {
            val destination = File(Environment.getExternalStorageDirectory(), "$fileName.txt")
            viewModel.copyAllExistingLogsToSingleExternalStorageFile(destination)
        } else {
            showSystemMessage(text = "Enter valid file name")
        }
    }
}