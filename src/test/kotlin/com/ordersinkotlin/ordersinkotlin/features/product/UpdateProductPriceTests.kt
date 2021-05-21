package com.ordersinkotlin.ordersinkotlin.features.product

import com.ordersinkotlin.ordersinkotlin.domain.product.ProductsRepository
import com.ordersinkotlin.ordersinkotlin.seedwork.CommandHandler
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UpdateProductPriceTests {

    @Autowired
    lateinit var repository: ProductsRepository

    @Autowired
    lateinit var handler: CommandHandler.WithoutResult<UpdateProductPrice.Command>

    @Autowired
    lateinit var createProductHandler: CommandHandler<CreateProduct.Command, CreateProduct.Result>

    @Test
    fun `given existing product, when changing its price, price should be changed`() : Unit = runBlocking {
        val productId = givenExistingProduct();

        val command = UpdateProductPrice.Command(productId, 20.0)
        handler.handle(command)

        val changed = repository.findById(productId)

        Assertions.assertThat(changed.isPresent).isTrue
        Assertions.assertThat(changed.get().price).isEqualTo(command.newPrice)
    }

    private suspend fun givenExistingProduct() : String {
        val result = createProductHandler.handle(
            CreateProduct.Command(55.0)
        )

        return result.productId
    }
}