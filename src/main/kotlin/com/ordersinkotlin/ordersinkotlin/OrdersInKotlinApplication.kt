package com.ordersinkotlin.ordersinkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrdersInKotlinApplication

fun main(args: Array<String>) {
    runApplication<OrdersInKotlinApplication>(*args)
}