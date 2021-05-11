package com.ordersinkotlin.ordersinkotlin.seedwork

interface CommandHandler<TCommand, TResult> {
    suspend fun handle(command: TCommand) : TResult

    interface WithoutResult<TCommand> : CommandHandler<TCommand, Unit>
}