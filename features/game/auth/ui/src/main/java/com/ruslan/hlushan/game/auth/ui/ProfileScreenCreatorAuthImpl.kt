package com.ruslan.hlushan.game.auth.ui

import com.github.terrakok.cicerone.Screen
import com.ruslan.hlushan.game.auth.ui.login.LoginScreen
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileScreen
import com.ruslan.hlushan.game.api.auth.AuthInteractor

object ProfileScreenCreatorAuthImpl {

    fun createProfileScreen(authInteractor: AuthInteractor): Screen =
            if (authInteractor.getUser() != null) {
                UserProfileScreen()
            } else {
                LoginScreen()
            }
}