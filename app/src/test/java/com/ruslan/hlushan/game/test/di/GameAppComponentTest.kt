package com.ruslan.hlushan.game.test.di

import com.ruslan.hlushan.core.api.di.ToolsProvider
import com.ruslan.hlushan.core.config.app.di.InitAppConfigProvider
import com.ruslan.hlushan.core.di.IBaseInjector
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.language.api.di.LanguagesInteractorProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.GameSettingsProvider
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.api.di.providers.TopInteractorProvider
import com.ruslan.hlushan.game.di.GameAppComponent
import com.ruslan.hlushan.game.settings.ui.di.SettingsOutScreenCreatorProvider
import com.ruslan.hlushan.third_party.androidx.room.utils.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import org.junit.Assert.assertTrue
import org.junit.Test

class GameAppComponentTest {

    @Test
    fun verifyGameAppComponentImplements() {
        assertTrue(IBaseInjector::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(InitAppConfigProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(ManagersProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(LoggersProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(LanguagesInteractorProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(SchedulersManagerProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(UiCoreProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(FragmentManagerConfiguratorProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(UiRoutingProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(ToolsProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(DatabaseViewInfoListProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(UserErrorMapperProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(AuthInteractorProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(GameSettingsProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(PlayRecordsInteractorProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(TopInteractorProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(RecordsUseCasesProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
        assertTrue(SettingsOutScreenCreatorProvider::class.java.isAssignableFrom(GameAppComponent::class.java))
    }
}