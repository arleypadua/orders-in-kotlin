package com.ordersinkotlin.ordersinkotlin.features.product

import com.ordersinkotlin.ordersinkotlin.domain.product.ProductsRepository
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CreateProductTests {

    @Autowired
    lateinit var repository: ProductsRepository

    @Autowired
    lateinit var handler: CommandHandler<CreateProduct.Command, CreateProduct.Result>

    @Test
    fun `given product with price, when creating, should be created`() : Unit = runBlocking {
        val command = CreateProduct.Command(55.0)
        val result = handler.handle(command)

        val created = repository.findById(result.productId)

        assertThat(created.isPresent).isTrue
        assertThat(created.get().price).isEqualTo(command.price)
    }
}

