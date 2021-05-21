package com.ordersinkotlin.ordersinkotlin.crosscutting

import net.logstash.logback.argument.StructuredArguments.value
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

object LoggerMap {
    private val loggers: ConcurrentHashMap<Class<*>, Logger> = ConcurrentHashMap<Class<*>, Logger>()

    fun getOrCreate(javaClass: Class<*>): Logger = loggers.getOrPut(javaClass) {
        LoggerFactory.getLogger(javaClass)
    }
}

object LoggerFormat {
    private val paramsRegex = Regex("""\{([^}]+)}""", RegexOption.IGNORE_CASE)
    private val internedLogMap = ConcurrentHashMap<String, LogTemplate>()

    private const val LOG_VARIABLE_PLACEHOLDER = "{}"

    fun getTemplateFor(format: String): LogTemplate {
        if (!paramsRegex.containsMatchIn(format))
            return LogTemplate(format, listOf())

        return internedLogMap.getOrPut(format) {
            createLogTemplate(format)
        }
    }

    private fun createLogTemplate(format: String): LogTemplate {
        val matches = paramsRegex
            .findAll(format)
            .map {
                it.value
                    .replace("{", "")
                    .replace("}", "")
            }
            .toList()

        return LogTemplate(
            paramsRegex.replace(format, LOG_VARIABLE_PLACEHOLDER),
            matches
        )
    }
}

data class LogTemplate(val format: String, val variables: List<String>) {

    fun buildArgumentsFrom(arguments: Array<out Any>): Array<Any> {
        val outputArguments = mutableListOf<Any>()
        arguments.forEachIndexed { index, arg ->
            val variableName = variables.elementAtOrNull(index)

            if (variableName != null)
                outputArguments.add(value(variableName, arg))
            else
                outputArguments.add(arg)
        }

        return outputArguments.toTypedArray()
    }
}

/**
 * Gets the logger for the given class.
 *
 * The logs are store in a singleton, meaning that they're created only once per runtime
 *
 * @param T the type of the class that gives context to the log instance
 * @return A new or an existing logger
 */
inline fun <reified T> logger() = LoggerMap.getOrCreate(T::class.java)

/**
 * Gets the logger for the current class where this method is called.
 *
 * The logs are store in a singleton, meaning that they're created only once per runtime
 *
 * @return A new or an existing logger
 */
fun Any.logger() = LoggerMap.getOrCreate(this::class.java)

fun Logger.structuredTrace(format: String, vararg arguments: Any) {
    val template = LoggerFormat.getTemplateFor(format)
    val logArguments = template.buildArgumentsFrom(arguments)

    this.trace(template.format, *logArguments)
}

fun Logger.structuredDebug(format: String, vararg arguments: Any) {
    val template = LoggerFormat.getTemplateFor(format)
    val logArguments = template.buildArgumentsFrom(arguments)

    this.debug(template.format, *logArguments)
}

fun Logger.structuredInfo(format: String, vararg arguments: Any) {
    val template = LoggerFormat.getTemplateFor(format)
    val logArguments = template.buildArgumentsFrom(arguments)

    this.info(template.format, *logArguments)
}

fun Logger.structuredWarn(format: String, vararg arguments: Any) {
    val template = LoggerFormat.getTemplateFor(format)
    val logArguments = template.buildArgumentsFrom(arguments)

    this.warn(template.format, *logArguments)
}

fun Logger.structuredError(format: String, vararg arguments: Any) {
    val template = LoggerFormat.getTemplateFor(format)
    val logArguments = template.buildArgumentsFrom(arguments)

    this.error(template.format, *logArguments)
}