package com.ruslan.hlushan.core.ui.routing

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router

interface CiceroneOwner {

    val cicerone: Cicerone<out Router>
}