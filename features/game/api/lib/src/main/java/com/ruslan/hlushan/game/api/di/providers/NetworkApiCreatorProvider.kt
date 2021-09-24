package com.ruslan.hlushan.game.api.di.providers

import com.ruslan.hlushan.game.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.api.network.NonAuthorizedNetworkApiCreator

interface NonAuthorizedNetworkApiCreatorProvider {

    fun provideNonAuthorizedNetworkApiCreator(): NonAuthorizedNetworkApiCreator
}

interface AuthorizedNetworkApiCreatorProvider {

    fun provideAuthorizedNetworkApiCreator(): AuthorizedNetworkApiCreator
}