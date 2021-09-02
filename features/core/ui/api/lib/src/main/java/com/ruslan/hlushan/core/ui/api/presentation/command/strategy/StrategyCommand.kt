package com.ruslan.hlushan.core.ui.api.presentation.command.strategy

interface StrategyCommand {

    fun produceStrategy(): HandleStrategy
}