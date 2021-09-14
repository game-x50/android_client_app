package com.ruslan.hlushan.core.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.utils.NewIntentHandler
import com.ruslan.hlushan.core.ui.api.utils.OnBackPressedHandler
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import com.ruslan.hlushan.core.ui.routing.CiceroneOwner
import com.ruslan.hlushan.core.ui.routing.FlowFragmentLifecyclePluginObserver
import javax.inject.Inject

abstract class BaseAppActivity : BaseActivity(), CiceroneOwner {

    @Inject
    override lateinit var cicerone: Cicerone<Router>

    @Inject
    lateinit var viewModifier: ViewModifier

    @get:StyleRes
    protected abstract val appThemeReId: Int

    @get:LayoutRes
    protected open val layoutResId: Int
        get() = com.ruslan.hlushan.core.ui.layout.container.R.layout.app_layout_container

    @get:IdRes
    protected open val appContainerResId: Int
        get() = com.ruslan.hlushan.core.ui.layout.container.R.id.app_container

    private val currentFragment: Fragment? get() = supportFragmentManager.findFragmentById(appContainerResId)

    @UiMainThread
    protected abstract fun createNavigator(): Navigator

    @CallSuper
    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(FlowFragmentLifecyclePluginObserver(
                flowCicerone = cicerone,
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

        val handler = (currentFragment as? NewIntentHandler)
        handler?.onNewIntent(intent)
    }

    override fun onBackPressed() {
        val handler = (currentFragment as? OnBackPressedHandler)
        if (handler != null) {
            handler.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }
}