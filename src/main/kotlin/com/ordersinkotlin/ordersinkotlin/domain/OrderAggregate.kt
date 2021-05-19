package com.ordersinkotlin.ordersinkotlin.domain

import com.ordersinkotlin.ordersinkotlin.seedwork.DomainEvent
import com.ordersinkotlin.ordersinkotlin.seedwork.Entity
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class Order private constructor() : Entity() {

    companion object {
        fun draft(customer: Customer, id: UUID = UUID.randomUUID()): Order {
            val order = Order()
            order.id = id.toString()
            order.customer = customer

            return order
        }
    }

    @Id
    lateinit var id: String
        private set

    var discount = 0.0
        private set

    var status = OrderStatus.Draft
        private set

    var statusChangedAtUtc: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        private set


    lateinit var customer: Customer
        private set

    private var itemsCollection = mutableListOf<OrderItem>()

    @Version
    private var version: String? = null

    var items
        get() = itemsCollection.toList()
        private set(value) {
            itemsCollection.addAll(value)
        }

    fun add(item: OrderItem) {
        require(status == OrderStatus.Draft) {
            "Can only add items to orders in Draft. Current status is $status"
        }

        itemsCollection.add(item)
    }

    fun add(itemsToAdd: Iterable<OrderItem>) {
        require(status == OrderStatus.Draft) {
            "Can only add items to orders in Draft. Current status is $status"
        }

        itemsCollection.addAll(itemsToAdd)
    }

    fun applyDiscount(amount: Double) {
        require(status == OrderStatus.Draft) {
            "Can only apply a discount on orders in Draft. Current status is $status."
        }

        require(amount <= itemsCollection.sumOf { it.total() }) {
            "The amount of discount cannot be higher than the order total"
        }

        require(amount >= 0) {
            "The discount amount has to be positive"
        }

        discount = amount
    }

    fun place(placedAtUtc: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)) {
        require(status == OrderStatus.Draft) {
            "Can only place orders that are in Draft. Current status is $status."
        }

        require(itemsCollection.any()) {
            "Can only place an order with items"
        }

        status = OrderStatus.Placed
        statusChangedAtUtc = placedAtUtc

        raise(OrderPlacedEvent(id, placedAtUtc))
    }

    fun ship(shippedAtUtc: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)) {
        require(status == OrderStatus.Placed) {
            "Can only ship orders that were placed. Current status is $status."
        }

        status = OrderStatus.Shipped
        statusChangedAtUtc = shippedAtUtc

        raise(OrderShippedEvent(id, shippedAtUtc))
    }

    fun cancel(cancelledAtUtc: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)) {
        require(status.isCancellable()) {
            "Cannot cancel order in status $status"
        }

        status = OrderStatus.Canceled
        statusChangedAtUtc = cancelledAtUtc

        raise(OrderCancelledEvent(id, cancelledAtUtc))
    }

    fun markDelivered(deliveredAtUtc: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)) {
        require(status == OrderStatus.Shipped) {
            "Cannot mark as delivered if status is not 'Shipped'. Current status is $status"
        }

        status = OrderStatus.Delivered
        statusChangedAtUtc = deliveredAtUtc

        raise(OrderDeliveredEvent(id, deliveredAtUtc))
    }
}

class OrderItem(
    val product: Product,
    val unitPrice: Double,
    val quantity: Int
) {
    init {
        require(unitPrice > 0) {
            "Unit price has to be higher than 0"
        }

        require(quantity > 0) {
            "Quantity has to be higher than 0"
        }
    }

    fun total() = unitPrice * quantity
}

enum class OrderStatus {
    Draft {
        override fun isCancellable() = true
    },
    Placed {
        override fun isCancellable() = true
    },
    Shipped {
        override fun isCancellable() = false
    },
    Delivered {
        override fun isCancellable() = false
    },
    Canceled {
        override fun isCancellable() = true
    };

    abstract fun isCancellable(): Boolean
}

data class Product(val id: UUID)
data class Customer(val id: UUID)

sealed class OrderStatusChangedEvent(val orderId: String, val changedAtUtc: LocalDateTime) : DomainEvent
class OrderPlacedEvent(orderId: String, changedAtUtc: LocalDateTime) : OrderStatusChangedEvent(orderId, changedAtUtc)
class OrderShippedEvent(orderId: String, changedAtUtc: LocalDateTime) : OrderStatusChangedEvent(orderId, changedAtUtc)
class OrderDeliveredEvent(orderId: String, changedAtUtc: LocalDateTime) : OrderStatusChangedEvent(orderId, changedAtUtc)
class OrderCancelledEvent(orderId: String, changedAtUtc: LocalDateTime) : OrderStatusChangedEvent(orderId, changedAtUtc)

interface OrdersRepository : PagingAndSortingRepository<Order, String>