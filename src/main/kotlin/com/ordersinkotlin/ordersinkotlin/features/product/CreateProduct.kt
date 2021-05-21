package com.ordersinkotlin.ordersinkotlin.features.product

import com.ordersinkotlin.ordersinkotlin.domain.product.Product
import com.ordersinkotlin.ordersinkotlin.domain.product.ProductsRepository
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import com.ordersinkotlin.ordersinkotlin.seedwork.UnitOfWork
import com.ordersinkotlin.ordersinkotlin.seedwork.commitTo
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.coRouter

class CreateProduct {
    data class Command(val price: Double)
    data class Result(val productId: String)

    @Configuration
    class Endpoint {
        @Bean
        fun createProductEndpoint(handler: Handler) = coRouter {
            POST("/products") {
                val request = it.awaitBody<Command>()
                val result = handler.handle(request)

                ServerResponse
                    .ok()
                    .bodyValue(result)
                    .awaitSingle()
            }
        }
    }

    @Component
    class Handler(
        private val repository: ProductsRepository,
        private val uow: UnitOfWork
    ) : CommandHandler<Command, Result> {
        override suspend fun handle(command: Command): Result {
            val product = Product.new(command.price)
            uow.commitTo<ProductsRepository>(product)

            return Result(product.id)
        }
    }
}