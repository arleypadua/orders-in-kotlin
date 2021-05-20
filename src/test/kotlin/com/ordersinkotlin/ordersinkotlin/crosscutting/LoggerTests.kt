package com.ordersinkotlin.ordersinkotlin.crosscutting

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LoggerTests {

    @Test
    fun `getting logger twice, should have same instance`() {
        val logger = logger()
        val otherLogger = logger<LoggerTests>()

        assertThat(logger).isEqualTo(otherLogger)
    }

    @Test
    fun `logger within different objects, should share same logger instance`() {
        val foo1 = Foo("bar1")
        val foo2 = Foo("bar2")

        assertThat(foo1.logger).isEqualTo(foo2.logger)
    }

    class Foo(val bar: String) {
        val logger = logger()
    }
}