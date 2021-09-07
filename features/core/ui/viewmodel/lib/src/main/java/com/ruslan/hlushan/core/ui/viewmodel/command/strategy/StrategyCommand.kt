package com.ruslan.hlushan.core.ui.viewmodel.command.strategy

interface StrategyCommand {

    fun produceStrategy(): HandleStrategy
}