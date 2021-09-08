package com.ruslan.hlushan.core.ui.lifecycle.test.utils

import androidx.lifecycle.Lifecycle
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.lifecycle.dispatchEventForAll
import org.junit.Assert.assertNull
import org.junit.Assert.fail

open class LifecyclePluginObserverOwnerFragmentStub : LifecyclePluginObserver.Owner, LifecyclePluginObserver {

    private val mutableLifecyclePluginObservers: MutableList<LifecyclePluginObserver> = mutableListOf()

    val lifecyclePluginObservers: List<LifecyclePluginObserver> get() = mutableLifecyclePluginObservers

    final override var currentState: Lifecycle.State? = null
        private set

    var detailState = LifecyclePluginObserverOwnerFragmentStub.DetailState.BEFORE_ON_BEFORE_SUPER_ATTACH
        private set

    override fun addLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        mutableLifecyclePluginObservers.add(observer)
    }

    override fun removeLifecyclePluginObserver(observer: LifecyclePluginObserver) {
        mutableLifecyclePluginObservers.remove(observer)
    }

    override fun onBeforeSuperAttach() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperAttach)
        currentState = null

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_ATTACH
    }

    override fun onAfterSuperAttach() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperAttach)
        currentState = null

        detailState = DetailState.AFTER_ON_AFTER_SUPER_ATTACH
    }

    override fun onBeforeSuperCreate() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreate)
        currentState = null

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_CREATE
    }

    override fun onAfterSuperCreate() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperCreate)
        currentState = null

        detailState = DetailState.AFTER_ON_AFTER_SUPER_CREATE
    }

    override fun onBeforeSuperCreateView() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperCreateView)
        currentState = Lifecycle.State.INITIALIZED

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW
    }

    override fun onAfterSuperViewCreated() {
        currentState = Lifecycle.State.INITIALIZED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperViewCreated)
        currentState = Lifecycle.State.INITIALIZED

        detailState = DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW
    }

    fun finishCreateProcess() {
        currentState = Lifecycle.State.CREATED

        detailState = DetailState.AFTER_FINISH_CREATE_PROCESS
    }

    override fun onBeforeSuperStart() {
        currentState = Lifecycle.State.CREATED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperStart)
        currentState = Lifecycle.State.CREATED

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_START
    }

    override fun onAfterSuperStart() {
        currentState = Lifecycle.State.CREATED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperStart)
        currentState = Lifecycle.State.CREATED

        detailState = DetailState.AFTER_ON_AFTER_SUPER_START
    }

    fun finishStartProcess() {
        currentState = Lifecycle.State.STARTED

        detailState = DetailState.AFTER_FINISH_START_PROCESS
    }

    override fun onBeforeSuperResume() {
        currentState = Lifecycle.State.STARTED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperResume)
        currentState = Lifecycle.State.STARTED

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_RESUME
    }

    override fun onAfterSuperResume() {
        currentState = Lifecycle.State.STARTED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperResume)
        currentState = Lifecycle.State.STARTED

        detailState = DetailState.AFTER_ON_AFTER_SUPER_RESUME
    }

    fun finishResumeProcess() {
        currentState = Lifecycle.State.RESUMED

        detailState = DetailState.AFTER_FINISH_RESUME_PROCESS
    }

    fun startPauseProcess() {
        currentState = Lifecycle.State.STARTED

        detailState = DetailState.AFTER_START_PAUSE_PROCESS
    }

    override fun onBeforeSuperPause() {
        currentState = Lifecycle.State.STARTED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperPause)
        currentState = Lifecycle.State.STARTED

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_PAUSE
    }

    override fun onAfterSuperPause() {
        currentState = Lifecycle.State.STARTED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperPause)
        currentState = Lifecycle.State.STARTED

        detailState = DetailState.AFTER_ON_AFTER_SUPER_PAUSE
    }

    fun startStopProcess() {
        currentState = Lifecycle.State.CREATED

        detailState = DetailState.AFTER_START_STOP_PROCESS
    }

    override fun onBeforeSuperStop() {
        currentState = Lifecycle.State.CREATED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperStop)
        currentState = Lifecycle.State.CREATED

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_STOP
    }

    override fun onAfterSuperStop() {
        currentState = Lifecycle.State.CREATED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperStop)
        currentState = Lifecycle.State.CREATED

        detailState = DetailState.AFTER_ON_AFTER_SUPER_STOP
    }

    fun startDestroyProcess() {
        currentState = Lifecycle.State.DESTROYED

        detailState = DetailState.AFTER_START_DESTROY_PROCESS
    }

    override fun onBeforeSuperDestroyView() {
        currentState = Lifecycle.State.DESTROYED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroyView)
        currentState = Lifecycle.State.DESTROYED

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW
    }

    override fun onAfterSuperDestroyView() {
        currentState = Lifecycle.State.DESTROYED
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroyView)
        currentState = Lifecycle.State.DESTROYED

        detailState = DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW
    }

    fun markAsUnInitialized() {
        currentState = null

        detailState = DetailState.AFTER_MARK_AS_UN_INITIALIZED
    }

    override fun onBeforeSuperDestroy() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDestroy)
        currentState = null

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_DESTROY
    }

    override fun onAfterSuperDestroy() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDestroy)
        currentState = null

        detailState = DetailState.AFTER_ON_AFTER_SUPER_DESTROY
    }

    override fun onBeforeSuperDetach() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperDetach)
        currentState = null

        detailState = DetailState.AFTER_ON_BEFORE_SUPER_DETACH
    }

    override fun onAfterSuperDetach() {
        currentState = null
        lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperDetach)
        currentState = null

        mutableLifecyclePluginObservers.clear()

        detailState = DetailState.AFTER_ON_AFTER_SUPER_DETACH
    }

    override fun onBeforeSuperSaveInstanceState() =
            lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onBeforeSuperSaveInstanceState)

    override fun onAfterSuperSaveInstanceState() =
            lifecyclePluginObservers.dispatchEventForAll(LifecyclePluginObserver::onAfterSuperSaveInstanceState)

    enum class DetailState {
        BEFORE_ON_BEFORE_SUPER_ATTACH,
        AFTER_ON_BEFORE_SUPER_ATTACH,
        AFTER_ON_AFTER_SUPER_ATTACH,
        AFTER_ON_BEFORE_SUPER_CREATE,
        AFTER_ON_AFTER_SUPER_CREATE,
        AFTER_ON_BEFORE_SUPER_CREATE_VIEW,
        AFTER_ON_AFTER_SUPER_CREATE_VIEW,
        AFTER_FINISH_CREATE_PROCESS,
        AFTER_ON_BEFORE_SUPER_START,
        AFTER_ON_AFTER_SUPER_START,
        AFTER_FINISH_START_PROCESS,
        AFTER_ON_BEFORE_SUPER_RESUME,
        AFTER_ON_AFTER_SUPER_RESUME,
        AFTER_FINISH_RESUME_PROCESS,
        AFTER_START_PAUSE_PROCESS,
        AFTER_ON_BEFORE_SUPER_PAUSE,
        AFTER_ON_AFTER_SUPER_PAUSE,
        AFTER_START_STOP_PROCESS,
        AFTER_ON_BEFORE_SUPER_STOP,
        AFTER_ON_AFTER_SUPER_STOP,
        AFTER_START_DESTROY_PROCESS,
        AFTER_ON_BEFORE_SUPER_DESTROY_VIEW,
        AFTER_ON_AFTER_SUPER_DESTROY_VIEW,
        AFTER_MARK_AS_UN_INITIALIZED,
        AFTER_ON_BEFORE_SUPER_DESTROY,
        AFTER_ON_AFTER_SUPER_DESTROY,
        AFTER_ON_BEFORE_SUPER_DETACH,
        AFTER_ON_AFTER_SUPER_DETACH
    }
}

fun LifecyclePluginObserverOwnerFragmentStub.from_init_go_to(
        detailState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    assertNull(this.currentState)
    this.go_to(detailState)
}

fun LifecyclePluginObserverOwnerFragmentStub.go_to(
        newState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    when {
        (this.detailState > newState) -> fail()
        (this.detailState < newState) -> {
            @Suppress("MaxLineLength")
            val orderedEvents = listOf(
                    (LifecyclePluginObserver::onBeforeSuperAttach to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH),
                    (LifecyclePluginObserver::onAfterSuperAttach to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH),

                    (LifecyclePluginObserver::onBeforeSuperCreate to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE),
                    (LifecyclePluginObserver::onAfterSuperCreate to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE),

                    (LifecyclePluginObserver::onBeforeSuperCreateView to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW),
                    (LifecyclePluginObserver::onAfterSuperViewCreated to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW),

                    (LifecyclePluginObserverOwnerFragmentStub::finishCreateProcess to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS),

                    (LifecyclePluginObserver::onBeforeSuperStart to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START),
                    (LifecyclePluginObserver::onAfterSuperStart to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START),

                    (LifecyclePluginObserverOwnerFragmentStub::finishStartProcess to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_START_PROCESS),

                    (LifecyclePluginObserver::onBeforeSuperResume to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME),
                    (LifecyclePluginObserver::onAfterSuperResume to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME),

                    (LifecyclePluginObserverOwnerFragmentStub::finishResumeProcess to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS),

                    (LifecyclePluginObserverOwnerFragmentStub::startPauseProcess to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_PAUSE_PROCESS),

                    (LifecyclePluginObserver::onBeforeSuperPause to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE),
                    (LifecyclePluginObserver::onAfterSuperPause to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE),

                    (LifecyclePluginObserverOwnerFragmentStub::startStopProcess to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS),

                    (LifecyclePluginObserver::onBeforeSuperStop to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP),
                    (LifecyclePluginObserver::onAfterSuperStop to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP),

                    (LifecyclePluginObserverOwnerFragmentStub::startDestroyProcess to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS),

                    (LifecyclePluginObserver::onBeforeSuperDestroyView to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW),
                    (LifecyclePluginObserver::onAfterSuperDestroyView to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW),

                    (LifecyclePluginObserverOwnerFragmentStub::markAsUnInitialized to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED),

                    (LifecyclePluginObserver::onBeforeSuperDestroy to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY),
                    (LifecyclePluginObserver::onAfterSuperDestroy to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY),

                    (LifecyclePluginObserver::onBeforeSuperDetach to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DETACH),
                    (LifecyclePluginObserver::onAfterSuperDetach to LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DETACH)
            )

            orderedEvents.takeWhile { (onEvent, nexStateOfEvent) ->
                if (this.detailState < nexStateOfEvent) {
                    onEvent(this)
                }

                (this.detailState != newState)
            }
        }
    }
}