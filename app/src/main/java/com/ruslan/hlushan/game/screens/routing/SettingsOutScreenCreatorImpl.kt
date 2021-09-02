package com.ruslan.hlushan.game.screens.routing

import com.github.terrakok.cicerone.Screen
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.auth.ui.ProfileScreenCreatorAuthImpl
import com.ruslan.hlushan.game.settings.ui.di.SettingsOutScreenCreator
import com.ruslan.hlushan.game.top.ui.games.TopGamesScreen
import javax.inject.Inject

internal class SettingsOutScreenCreatorImpl
@Inject
constructor(private val authInteractor: AuthInteractor) : SettingsOutScreenCreator {

    override fun createProfileScreen(): Screen = ProfileScreenCreatorAuthImpl.createProfileScreen(authInteractor)

    override fun createTopScreen(): Screen = TopGamesScreen()
}