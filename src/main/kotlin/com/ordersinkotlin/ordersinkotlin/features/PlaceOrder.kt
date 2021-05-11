package com.ordersinkotlin.ordersinkotlin.features

import com.ordersinkotlin.ordersinkotlin.features.data.OrderProduct
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import org.springframework.stereotype.Component
import java.util.*

class PlaceOrder {
    data class Command(val orderId: UUID)
    object Result

    @Component
    class Handler(
//    private val uow: UnitOfWork
    ) : CommandHandler<Command, Result> {
        override suspend fun handle(command: Command) : Result {
            TODO("Not yet implemented")
        }
    }
}