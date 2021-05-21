package com.ordersinkotlin.ordersinkotlin.features.product

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

class UpdateProductPrice {
    data class Command(val productId: String?, val newPrice: Double)

    @Configuration
    class Endpoint {
        @Bean
        fun updateProductPriceEndpoint(handler: Handler) = coRouter {
            POST("/products/:productId/price") {
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
    ) : CommandHandler.WithoutResult<Command> {
        override suspend fun handle(command: Command) {
            val product = repository.findById(command.productId!!)
                .orElseThrow { IllegalArgumentException("Product does not exist") }

            product.changePrice(command.newPrice)

            uow.commitTo<ProductsRepository>(product)
        }
    }
}