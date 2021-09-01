package com.ruslan.hlushan.core.ui.api.presentation.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.R
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.FlowFragmentLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import javax.inject.Inject

abstract class BaseAppActivity : BaseActivity() {

    @Inject
    lateinit var appCicerone: Cicerone<Router>

    @Inject
    lateinit var viewModifier: ViewModifier

    @get:StyleRes
    protected abstract val appThemeReId: Int

    @get:LayoutRes
    protected open val layoutResId: Int
        get() = R.layout.app_layout_container

    @get:IdRes
    protected open val appContainerResId: Int
        get() = R.id.app_container

    private val currentFragment: BaseFragment?
        get() = (supportFragmentManager.findFragmentById(appContainerResId) as? BaseFragment)

    @UiMainThread
    protected abstract fun createNavigator(): Navigator

    @CallSuper
    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(FlowFragmentLifecyclePluginObserver(
                flowCicerone = appCicerone,
                createFlowNavigator = this::createNavigator
        ))
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(appThemeReId)

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setUpFirstAppScreen()
        }
    }

    @UiMainThread
    @CallSuper
    override fun initContentView() =
            setContentView(viewModifier.modify(layoutInflater.inflate(layoutResId, null)))

    @UiMainThread
    protected abstract fun setUpFirstAppScreen()

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        currentFragment?.onNewIntent(intent)
    }
}