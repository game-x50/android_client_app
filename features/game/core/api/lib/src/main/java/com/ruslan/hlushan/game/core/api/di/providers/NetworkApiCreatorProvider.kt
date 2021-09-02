package com.ruslan.hlushan.game.core.api.di.providers

import com.ruslan.hlushan.game.core.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.core.api.network.NonAuthorizedNetworkApiCreator

/**
 * @author Ruslan Hlushan on 2019-07-26
 */

interface NonAuthorizedNetworkApiCreatorProvider {

    fun provideNonAuthorizedNetworkApiCreator(): NonAuthorizedNetworkApiCreator
}

interface AuthorizedNetworkApiCreatorProvider {

    fun provideAuthorizedNetworkApiCreator(): AuthorizedNetworkApiCreator
}