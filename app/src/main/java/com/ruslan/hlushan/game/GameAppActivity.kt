package com.ruslan.hlushan.game

import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.redmadrobot.e2e.decorator.EdgeToEdgeDecorator
import com.ruslan.hlushan.android.extensions.getContextCompatColor
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.activity.BaseAppActivity
import com.ruslan.hlushan.core.ui.api.utils.BottomMenuHolder
import com.ruslan.hlushan.core.ui.viewmodel.extensions.bindBaseViewModel
import com.ruslan.hlushan.game.di.getGameMainUiComponent
import com.ruslan.hlushan.third_party.androidx.insets.applyWindowTransparencyAfterSetContentView

internal class GameAppActivity : BaseAppActivity(), BottomMenuHolder {

    @get:StyleRes
    override val appThemeReId: Int
        get() = R.style.game_app_Theme

    private val viewModel: GameAppViewModel by bindBaseViewModel {
        getGameMainUiComponent().gameAppViewModelFactory().create(cicerone.router)
    }

    @UiMainThread
    override fun initDagger2() = getGameMainUiComponent().inject(this)

    @UiMainThread
    override fun createNavigator(): Navigator = AppNavigator(activity = this, containerId = this.appContainerResId)

    @UiMainThread
    override fun setUpFirstAppScreen() = viewModel.startFirstScreen()

    @UiMainThread
    override fun showBottomMenu(show: Boolean) {
        (supportFragmentManager.findFragmentById(appContainerResId) as? BottomMenuHolder)?.showBottomMenu(show)
    }

    @UiMainThread
    override fun applyWindowTransparencyFlags() {
        @ColorInt val statusBarColorInt = getContextCompatColor(R.color.game_app_statusBarColor)

        EdgeToEdgeDecorator
                .updateConfig {
                    isEdgeToEdgeEnabled = true
                    
                    statusBarEdgeToEdgeColor = statusBarColorInt
                    statusBarCompatibilityColor = statusBarColorInt
                }
                .apply(context = this, window = this.window)
    }

    @UiMainThread
    override fun initContentView() {
        super.initContentView()
        this.applyWindowTransparencyAfterSetContentView(findViewById(appContainerResId))
    }
}