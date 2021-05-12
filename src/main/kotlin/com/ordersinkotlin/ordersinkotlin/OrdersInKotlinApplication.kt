package com.ordersinkotlin.ordersinkotlin

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.coRouter

@SpringBootApplication
class OrdersInKotlinApplication

fun main(args: Array<String>) {
    runApplication<OrdersInKotlinApplication>(*args)
}