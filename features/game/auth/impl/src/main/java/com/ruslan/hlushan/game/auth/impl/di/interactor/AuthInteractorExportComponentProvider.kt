package com.ruslan.hlushan.game.auth.impl.di.interactor

import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.auth.impl.di.repo.AuthRepoHolder
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AuthInteractorModule::class],
        dependencies = [
            RecordsUseCasesProvider::class,
            SchedulersManagerProvider::class
        ]
)
interface AuthInteractorExportComponentProvider : AuthInteractorProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance authRepoHolder: AuthRepoHolder,
                recordsUseCasesProvider: RecordsUseCasesProvider,
                schedulersProvider: SchedulersManagerProvider
        ): AuthInteractorProvider
    }

    object Initializer {
        fun init(
                recordsUseCasesProvider: RecordsUseCasesProvider,
                authRepoHolder: AuthRepoHolder,
                schedulersProvider: SchedulersManagerProvider
        ): AuthInteractorProvider =
                DaggerAuthInteractorExportComponentProvider.factory()
                        .create(
                                recordsUseCasesProvider = recordsUseCasesProvider,
                                authRepoHolder = authRepoHolder,
                                schedulersProvider = schedulersProvider
                        )
    }
}