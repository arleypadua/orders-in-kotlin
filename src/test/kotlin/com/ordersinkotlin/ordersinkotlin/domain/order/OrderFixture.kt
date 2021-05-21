package com.ordersinkotlin.ordersinkotlin.domain.order

import java.util.*

object OrderFixture {
    fun getDraftOrder(includeItem: Boolean = false): Order {
        val order = Order.draft(Customer(UUID.randomUUID()), UUID.randomUUID())
        if (includeItem)
            order.add(
                OrderItem(
                    Product(UUID.randomUUID()),
                    50.0,
                    2
                )
            )

        return order
    }

    fun getPlacedOrder(): Order {
        val order = getDraftOrder(includeItem = true)
        order.place()
        return order
    }

    fun getShippedOrder(): Order {
        val order = getPlacedOrder()
        order.ship()
        return order
    }
}