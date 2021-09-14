@file:Suppress("PackageNaming")

package com.ruslan.hlushan.game.play.ui.screens.game.new_game

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.viewmodel.extensions.bindBaseViewModel
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.ui.screens.di.gamePlayUiComponent
import com.ruslan.hlushan.game.play.ui.screens.game.PlayGameFragment

private const val KEY_GAME_SIZE = "KEY_GAME_SIZE"

internal class NewGameFragment : PlayGameFragment<NewGameViewModel>() {

    override val viewModel: NewGameViewModel by bindBaseViewModel {
        gamePlayUiComponent().newGameViewModelFactory().create(parentRouter)
    }

    private val selectedGameSize: GameSize
        get() = (arguments?.getInt(KEY_GAME_SIZE)
                         ?.let { countRowsAndColumns -> GameSize.fromCountRowsAndColumns(countRowsAndColumns) }
                 ?: GameSize.SMALL)

    @UiMainThread
    override fun injectDagger2() = gamePlayUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.playScreenGameView?.setNewSize(selectedGameSize)
    }
}

internal class NewGameScreen(private val gameSize: GameSize) : FragmentScreen {

    override val screenKey: String get() = "NewGameScreen"

    override fun createFragment(factory: FragmentFactory): Fragment {
        val fragment = NewGameFragment()
        val bundle = Bundle(1)
        bundle.putInt(KEY_GAME_SIZE, gameSize.countRowsAndColumns)
        fragment.arguments = bundle
        return fragment
    }
}