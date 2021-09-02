@file:Suppress("PackageNaming")

package com.ruslan.hlushan.game.play.ui.game.continue_game

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.play.ui.di.getGamePlayUiComponent
import com.ruslan.hlushan.game.play.ui.dto.GameRecordParcelable
import com.ruslan.hlushan.game.play.ui.dto.toParcelable
import com.ruslan.hlushan.game.play.ui.game.PlayGameFragment

private const val KEY_GAME_RECORD = "KEY_GAME_RECORD"

internal class ContinueGameFragment : PlayGameFragment<ContinueGameViewModel>() {

    override val viewModel: ContinueGameViewModel by bindBaseViewModel {
        getGamePlayUiComponent().continueGameViewModelFactory().create(
                router = parentRouter,
                continuedGameRecord = arguments?.getParcelable<GameRecordParcelable>(KEY_GAME_RECORD)?.toOriginal()
        )
    }

    @UiMainThread
    override fun injectDagger2() = getGamePlayUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ifNotNull(viewModel.continuedGameRecord) { gameRecord ->
            binding?.playScreenGameView?.setGameViewState(gameRecord.gameState)
        }
    }
}

internal class ContinueGameScreen(private val record: GameRecord) : FragmentScreen {

    override val screenKey: String get() = "ContinueGameScreen"

    override fun createFragment(factory: FragmentFactory): Fragment {
        val fragment = ContinueGameFragment()
        val bundle = Bundle(1)
        bundle.putParcelable(KEY_GAME_RECORD, record.toParcelable())
        fragment.arguments = bundle
        return fragment
    }
}