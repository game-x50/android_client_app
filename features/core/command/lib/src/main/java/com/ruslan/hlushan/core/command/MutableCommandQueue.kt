package com.ruslan.hlushan.core.command

import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.thread.SingleThreadChecker
import com.ruslan.hlushan.core.thread.SingleThreadSafety
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.thread.checkThread
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

@SingleThreadSafety
class MutableCommandQueue<Command : StrategyCommand> : CommandQueue<Command> {

    private val threadChecker: ThreadChecker = SingleThreadChecker()

    private val newCommandSubject = PublishSubject.create<Command>()

    override var commands: List<Command> = listOf<Command>()
        private set

    override fun observeNewCommand(): Observable<Command> = newCommandSubject

    override fun notifyAfterCommandExecute(incomingCommand: Command) {
        threadChecker.checkThread()

        commands = incomingCommand.produceStrategy().afterApply(
                currentState = commands,
                incomingCommand = incomingCommand
        )
    }

    fun add(incomingCommand: Command) {
        threadChecker.checkThread()

        commands = incomingCommand.produceStrategy().beforeApply(
                currentState = commands,
                incomingCommand = incomingCommand
        )

        newCommandSubject.onNext(incomingCommand)
    }
}