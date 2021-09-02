package com.ruslan.hlushan.core.api.managers

class CompositeUserErrorMapper(
        private val simpleProducers: List<SimpleUserErrorMapper>
) {

    fun produceUserMessage(error: Throwable): String =
            (simpleProducers
                     .asSequence()
                     .map { producer -> producer.produceUserMessage(error) }
                     .filterNotNull()
                     .firstOrNull()
             ?: error.message.orEmpty())
}