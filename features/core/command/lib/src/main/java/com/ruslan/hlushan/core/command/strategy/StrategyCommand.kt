package com.ruslan.hlushan.core.command.strategy

interface StrategyCommand {

    fun produceStrategy(): HandleStrategy
}