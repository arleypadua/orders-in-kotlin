package com.ordersinkotlin.ordersinkotlin.domain.order

import java.util.*

object OrderFixture {
    fun getDraftOrder(includeItem: OrderItem? = null): Order {
        val order = Order.draft(Customer(UUID.randomUUID().toString()), UUID.randomUUID())
        includeItem?.let {
            order.add(it)
        }

        return order
    }

    fun getPlacedOrder(): Order {
        val order = getDraftOrder(getRandomOrderItem())
        order.place()
        return order
    }

    fun getShippedOrder(): Order {
        val order = getPlacedOrder()
        order.ship()
        return order
    }

    fun getRandomOrderItem() = OrderItem(
        Product(UUID.randomUUID().toString()),
        20.0,
        1
    )
}