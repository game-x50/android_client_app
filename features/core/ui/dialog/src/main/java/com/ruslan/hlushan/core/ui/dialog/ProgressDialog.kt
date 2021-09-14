package com.ruslan.hlushan.core.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommand
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.views.LoaderView
import com.ruslan.hlushan.third_party.androidx.fragment.extensions.showNowSafety

private const val ARG_MESSAGE = "ARG_MESSAGE"
private const val TAG_PROGRESS_DIALOG = "TAG_PROGRESS_DIALOG"

internal class ProgressDialog : BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.dialog_progress

    private var message: String? = null

    //todo
    @Suppress("UnusedPrivateMember")
    private var loaderView: LoaderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ProgressDialogTheme)
        isCancelable = false

        message = arguments?.getString(ARG_MESSAGE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        updateViews()
    }

    @UiMainThread
    fun updateMessage(newMessage: String?) {
        message = newMessage
        arguments?.putString(ARG_MESSAGE, newMessage)
        updateViews()
    }

    @Suppress("UnusedPrivateMember")
    private fun initViews(root: View) {
//        loaderView = root.findViewById(R.id.) todo
    }

    private fun updateViews() = Unit

    class ShowCommand(private val message: String?) : DialogCommand() {

        override val tag: String get() = TAG_PROGRESS_DIALOG

        @UiMainThread
        override fun execute(fragmentManager: FragmentManager) {
            val progressDialog = find(fragmentManager)
            if (progressDialog == null) {
                createProgressDialog(message)
                        .showNowSafety(fragmentManager, tag)
            } else {
                progressDialog.updateMessage(message)
            }
        }

        private fun find(fragmentManager: FragmentManager): ProgressDialog? =
                (fragmentManager.findFragmentByTag(tag) as? ProgressDialog)
    }
}

private fun createProgressDialog(message: String?): ProgressDialog =
        ProgressDialog().apply {
            val args = Bundle(1)
            args.putString(ARG_MESSAGE, message)
            arguments = args
        }

@UiMainThread
fun DialogCommandsHandler.Owner.showSimpleProgress(show: Boolean) =
        if (show) {
            showProgress(message = null)
        } else {
            hideProgress()
        }

@UiMainThread
fun DialogCommandsHandler.Owner.showProgress(message: String? = null) =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ProgressDialog.ShowCommand(message))

@UiMainThread
fun DialogCommandsHandler.Owner.hideProgress() =
        this.dialogCommandsHandler.removeFromQueueOrExecuteClose(TAG_PROGRESS_DIALOG)