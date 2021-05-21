package com.ordersinkotlin.ordersinkotlin.domain.product

import com.ordersinkotlin.ordersinkotlin.seedwork.Entity
import org.springframework.data.annotation.Id
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class Product private constructor() : Entity() {
    companion object {
        fun new(price: Double): Product {
            assertPriceIsValid(price)

            val product = Product()
            product.price = price

            return product;
        }

        private fun assertPriceIsValid(price: Double) = require(price > 0) {
            "Price has to be greater than 0. Found price $price"
        }
    }

    @Id
    lateinit var id: String
        private set

    var price: Double = 0.0
        private set

    var priceChangedAtUtc: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        private set

    fun changePrice(newPrice: Double) {
        assertPriceIsValid(newPrice)

        price = newPrice
        priceChangedAtUtc = LocalDateTime.now(ZoneOffset.UTC)
    }
}

interface ProductsRepository : PagingAndSortingRepository<Product, String>