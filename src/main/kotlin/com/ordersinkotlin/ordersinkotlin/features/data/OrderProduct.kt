package com.ordersinkotlin.ordersinkotlin.features.data

import com.ordersinkotlin.ordersinkotlin.domain.order.OrderItem
import com.ordersinkotlin.ordersinkotlin.domain.order.Product
import java.util.*

data class OrderProduct(val productId: String, val quantity: Int)

fun OrderProduct.asOrderItem(currentUnitPrice: Double) =
    OrderItem(Product(this.productId), currentUnitPrice, this.quantity)