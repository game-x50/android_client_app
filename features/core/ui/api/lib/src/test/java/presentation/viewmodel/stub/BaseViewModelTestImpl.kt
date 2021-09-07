package presentation.viewmodel.stub

import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.presentation.viewmodel.BaseViewModel
import io.reactivex.disposables.Disposable

class BaseViewModelTestImpl(
        threadChecker: ThreadChecker
) : BaseViewModel(
        appLogger = EmptyAppLoggerImpl(),
        threadChecker = threadChecker
) {

    var onAfterFirstAttachViewCalledTimes: Int = 0
        private set

    override fun onAfterFirstAttachView() {
        super.onAfterFirstAttachView()

        onAfterFirstAttachViewCalledTimes++
    }

    fun callJoinWhileViewAttached(disposable: Disposable) =
            disposable.joinWhileViewAttached()

    fun callJoinUntilDestroy(disposable: Disposable) =
            disposable.joinUntilDestroy()
}