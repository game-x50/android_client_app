package com.ruslan.hlushan.game.test.di

import androidx.work.Configuration
import com.ruslan.hlushan.core.di.InjectorHolder
import com.ruslan.hlushan.core.impl.BaseApplication
import com.ruslan.hlushan.game.GameApp
import org.junit.Assert
import org.junit.Test

class GameAppTest {

    @Test
    fun verifyGameAppImplements() {
        Assert.assertTrue(BaseApplication::class.java.isAssignableFrom(GameApp::class.java))
        Assert.assertTrue(InjectorHolder::class.java.isAssignableFrom(GameApp::class.java))
        Assert.assertTrue(Configuration.Provider::class.java.isAssignableFrom(GameApp::class.java))
    }
}