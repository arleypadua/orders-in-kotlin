package com.ordersinkotlin.ordersinkotlin.crosscutting

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class LoggerTests {

    @Test
    fun `getting logger twice, should have same instance`() {
        val logger = logger()
        val otherLogger = logger<LoggerTests>()

        assertThat(logger).isEqualTo(otherLogger)
    }

    @Test
    fun `logger within different objects, should share same logger instance`() {
        val foo1 = Foo()
        val foo2 = Foo()

        assertThat(foo1.logger).isEqualTo(foo2.logger)
    }

    @Test
    fun `given log message, when logging, should build structure`() {
        val logger = logger()

        val templateString = "Test trace {testId} whatever {number}"

        logger.structuredInfo(templateString, UUID.randomUUID(), 25.05)

        val template = LoggerFormat.getTemplateFor(templateString)
        assertThat(template).isNotNull
        assertThat(template.format).isEqualTo("Test trace {} whatever {}")
        assertThat(template.variables).contains("testId", "number")
    }

    class Foo {
        val logger = logger()
    }
}