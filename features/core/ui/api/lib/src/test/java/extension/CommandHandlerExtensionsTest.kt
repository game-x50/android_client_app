@file:SuppressWarnings("MaxLineLength")

package extension

import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.api.test.utils.LifecyclePluginObserverOwnerFragmentStub
import com.ruslan.hlushan.core.ui.api.test.utils.from_init_go_to
import com.ruslan.hlushan.core.ui.viewmodel.command.CommandQueue
import com.ruslan.hlushan.core.ui.viewmodel.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.viewmodel.command.handleCommandQueue
import com.ruslan.hlushan.core.ui.viewmodel.command.strategy.StrategyCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import presentation.command.strategy.`create NOT empty CommandQueue`
import presentation.command.strategy.`create default real command`

@SuppressWarnings("LargeClass")
class CommandHandlerExtensionsTest {

    @Test
    fun `handleCommandQueue before onBeforeSuperAttach will NOT call handler on empty queue and NOT mutate queue`() =
            `assert handleCommandQueue will NOT call handler and NOT mutate queue`(
                    fragmentStub = LifecyclePluginObserverOwnerFragmentStub(),
                    commandQueue = MutableCommandQueue<StrategyCommand>()
            )

    @Test
    fun `handleCommandQueue before onBeforeSuperAttach will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `assert handleCommandQueue will NOT call handler and NOT mutate queue`(
                    fragmentStub = LifecyclePluginObserverOwnerFragmentStub(),
                    commandQueue = `create NOT empty CommandQueue`()
            )

    @Test
    fun `handleCommandQueue before onBeforeSuperAttach will NOT call handler and NOT mutate queue on update queue`() =
            `assert handleCommandQueue NOT call handler and NOT mutate queue on update queue`(
                    fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperAttach will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperAttach will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperAttach will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperAttach will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperAttach will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperAttach will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreate will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreate will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreate will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperCreate will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperCreate will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperCreate will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreateView will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreateView will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreateView will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperViewCreated will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperViewCreated will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperViewCreated will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after finishCreateProcess but before onBeforeSuperStart will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS
            )

    @Test
    fun `handleCommandQueue after finishCreateProcess but before onBeforeSuperStart will NOT call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS
            )

    @Test
    fun `handleCommandQueue after finishCreateProcess but before onBeforeSuperStart will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStart will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStart will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStart will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStart will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStart will NOT call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStart will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START
            )

    @Test
    fun `handleCommandQueue after finishStartProcess but before onBeforeSuperResume will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_START_PROCESS
            )

    @Test
    fun `handleCommandQueue after finishStartProcess but before onBeforeSuperResume will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_START_PROCESS
            )

    @Test
    fun `handleCommandQueue after finishStartProcess but before onBeforeSuperResume will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_START_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperResume will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperResume will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperResume will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after onAfterSuperResume will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after onAfterSuperResume will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after onAfterSuperResume will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after finishResumeProcess but before startPauseProcess will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS
            )

    @Test
    fun `handleCommandQueue after finishResumeProcess but before startPauseProcess will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS
            )

    @Test
    fun `handleCommandQueue after finishResumeProcess but before startPauseProcess will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS
            )

    @Test
    fun `handleCommandQueue after startPauseProcess but before onBeforeSuperPause will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_PAUSE_PROCESS
            )

    @Test
    fun `handleCommandQueue after startPauseProcess but before onBeforeSuperPause will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_PAUSE_PROCESS
            )

    @Test
    fun `handleCommandQueue after startPauseProcess but before onBeforeSuperPause will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_PAUSE_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperPause will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperPause will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperPause will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperPause will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperPause will call handler on NOT empty queue and mutate queue`() =
            `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperPause will call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after startStopProcess but before onBeforeSuperStop will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS
            )

    @Test
    fun `handleCommandQueue after startStopProcess but before onBeforeSuperStop will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS
            )

    @Test
    fun `handleCommandQueue after startStopProcess but before onBeforeSuperStop will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStop will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStop will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStop will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStop will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStop will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStop will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after startDestroyProcess but before onBeforeSuperDestroyView will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after startDestroyProcess but before onBeforeSuperDestroyView will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS
            )

    @Test
    fun `handleCommandQueue after startDestroyProcess but before onBeforeSuperDestroyView will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroyView will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroyView will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroyView will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroyView will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroyView will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroyView will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after markAsUnInitialized but before onBeforeSuperDestroy will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after markAsUnInitialized but before onBeforeSuperDestroy will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED
            )

    @Test
    fun `handleCommandQueue after markAsUnInitialized but before onBeforeSuperDestroy will NOT call handler and NOT mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroy will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroy will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroy will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroy will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroy will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroy will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDetach will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DETACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDetach will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DETACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDetach will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DETACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDetach will NOT call handler on empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DETACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDetach will NOT call handler on NOT empty queue and NOT mutate queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DETACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDetach will NOT call handler and mutate queue on update queue`() =
            `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DETACH
            )
}

private fun `handleCommandQueue after some lifecycle method will NOT call handler on empty queue and NOT mutate queue`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue will NOT call handler and NOT mutate queue`(
            fragmentStub = fragmentStub,
            commandQueue = MutableCommandQueue<StrategyCommand>()
    )
}

private fun `handleCommandQueue after some lifecycle method will NOT call handler on NOT empty queue and NOT mutate queue`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue will NOT call handler and NOT mutate queue`(
            fragmentStub = fragmentStub,
            commandQueue = `create NOT empty CommandQueue`()
    )
}

private fun `handleCommandQueue after some lifecycle method will NOT call handler and NOT mutate queue on update queue`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue NOT call handler and NOT mutate queue on update queue`(
            fragmentStub = fragmentStub
    )
}

private fun `handleCommandQueue after some lifecycle method will call handler on NOT empty queue and mutate queue`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue will call handler and mutate queue`(
            fragmentStub = fragmentStub
    )
}

private fun `handleCommandQueue after some lifecycle method will call handler and mutate queue on update queue`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue call handler and mutate queue on update queue`(
            fragmentStub = fragmentStub
    )
}

private fun `assert handleCommandQueue will NOT call handler and NOT mutate queue`(
        fragmentStub: LifecyclePluginObserver.Owner,
        commandQueue: CommandQueue<StrategyCommand>
) {
    val oldState = commandQueue.commands.copy()
    fragmentStub.handleCommandQueue(commandQueue) { receivedCommand ->
        fail()
    }
    assertEquals(oldState, commandQueue.commands)
}

private fun `assert handleCommandQueue will call handler and mutate queue`(
        fragmentStub: LifecyclePluginObserver.Owner
) {
    val commandQueue: CommandQueue<StrategyCommand> = `create NOT empty CommandQueue`()
    val commandQueueInitSize = commandQueue.commands.size

    val expectedState = commandQueue.commands.copy().toMutableList()

    var handlerCalledCounter = 0
    fragmentStub.handleCommandQueue(commandQueue) { receivedCommand ->
        handlerCalledCounter++
        assertEquals(expectedState.firstOrNull(), receivedCommand)
        assertEquals(expectedState, commandQueue.commands)

        expectedState.removeFirst()
    }

    assertTrue(expectedState.isEmpty())
    assertEquals(expectedState, commandQueue.commands)
    assertEquals(commandQueueInitSize, handlerCalledCounter)
}

private fun `assert handleCommandQueue NOT call handler and NOT mutate queue on update queue`(
        fragmentStub: LifecyclePluginObserver.Owner
) {
    val commandQueue = `create NOT empty CommandQueue`()

    val expectedState = commandQueue.commands.copy().toMutableList()

    fragmentStub.handleCommandQueue(commandQueue) { receivedCommand ->
        fail()
    }

    val addedAfterHandleCommand = `create default real command`()
    expectedState.add(addedAfterHandleCommand)
    commandQueue.add(addedAfterHandleCommand)

    assertEquals(expectedState, commandQueue.commands)
}

private fun `assert handleCommandQueue call handler and mutate queue on update queue`(
        fragmentStub: LifecyclePluginObserver.Owner
) {
    val commandQueue = `create NOT empty CommandQueue`()

    val expectedState = commandQueue.commands.copy().toMutableList()

    var expectedCalledCounter = expectedState.size

    var handlerCalledCounter = 0

    fragmentStub.handleCommandQueue(commandQueue) { receivedCommand ->
        handlerCalledCounter++
        assertEquals(expectedState.firstOrNull(), receivedCommand)
        assertEquals(expectedState, commandQueue.commands)

        expectedState.removeFirst()
    }

    assertTrue(expectedState.isEmpty())
    assertEquals(expectedState, commandQueue.commands)
    assertEquals(expectedCalledCounter, handlerCalledCounter)

    val addedAfterHandleCommand = `create default real command`()
    expectedState.add(addedAfterHandleCommand)
    commandQueue.add(addedAfterHandleCommand)
    expectedCalledCounter++

    assertTrue(expectedState.isEmpty())
    assertEquals(expectedState, commandQueue.commands)
    assertEquals(expectedCalledCounter, handlerCalledCounter)
}