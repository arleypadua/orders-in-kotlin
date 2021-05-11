package com.ordersinkotlin.ordersinkotlin.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderAggregateTests {
    @Test
    fun `order in draft and items added, when applying discount, discount should be applied`() {
        val order = OrderFixture.getDraftOrder(includeItem = true)

        order.applyDiscount(50.0)

        assert(order.discount == 50.0)
    }

    @Test
    fun `order in draft and no items added, when applying discount, error should be thrown`() {
        val order = OrderFixture.getDraftOrder()

        assertThrows<IllegalArgumentException> {
            order.applyDiscount(50.0)
        }
    }

    @Test
    fun `order in draft, when applying discount higher then total, error should be thrown`() {
        val order = OrderFixture.getDraftOrder(includeItem = true)

        assertThrows<IllegalArgumentException> {
            order.applyDiscount(200.0)
        }
    }

    @Test
    fun `order placed, when applying discount, error should be thrown`() {
        val order = OrderFixture.getPlacedOrder()

        assertThrows<IllegalArgumentException> {
            order.applyDiscount(50.0)
        }
    }

    @Test
    fun `order in draft, when placing, order should be placed`() {
        val order = OrderFixture.getDraftOrder(includeItem = true)
        order.place()

        assert(order.status == OrderStatus.Placed)
        assert(
            order.peekDomainEvents()
                .any { it is OrderPlacedEvent }
        )
    }

    @Test
    fun `order in draft, when cancelling, order should be cancelled`() {
        val order = OrderFixture.getDraftOrder(includeItem = true)
        order.cancel()

        assert(order.status == OrderStatus.Canceled)
        assert(
            order.peekDomainEvents()
                .any { it is OrderCancelledEvent }
        )
    }

    @Test
    fun `order shipped, when cancelling, error should be thrown`() {
        val order = OrderFixture.getShippedOrder()

        assertThrows<IllegalArgumentException> {
            order.cancel()
        }
    }


}

