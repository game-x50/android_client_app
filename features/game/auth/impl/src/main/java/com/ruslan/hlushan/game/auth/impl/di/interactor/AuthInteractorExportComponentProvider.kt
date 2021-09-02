package com.ruslan.hlushan.game.auth.impl.di.interactor

import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.auth.impl.di.repo.AuthRepoHolder
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AuthInteractorModule::class],
        dependencies = [
            RecordsUseCasesProvider::class,
            SchedulersProvider::class
        ]
)
interface AuthInteractorExportComponentProvider : AuthInteractorProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance authRepoHolder: AuthRepoHolder,
                recordsUseCasesProvider: RecordsUseCasesProvider,
                schedulersProvider: SchedulersProvider
        ): AuthInteractorProvider
    }

    object Initializer {
        fun init(
                recordsUseCasesProvider: RecordsUseCasesProvider,
                authRepoHolder: AuthRepoHolder,
                schedulersProvider: SchedulersProvider
        ): AuthInteractorProvider =
                DaggerAuthInteractorExportComponentProvider.factory()
                        .create(
                                recordsUseCasesProvider = recordsUseCasesProvider,
                                authRepoHolder = authRepoHolder,
                                schedulersProvider = schedulersProvider
                        )
    }
}