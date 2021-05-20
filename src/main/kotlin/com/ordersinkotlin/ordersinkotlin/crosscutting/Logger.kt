package com.ordersinkotlin.ordersinkotlin.crosscutting

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

object LoggerMap {
    private val loggers: ConcurrentHashMap<Class<*>, Logger> = ConcurrentHashMap<Class<*>, Logger>()

    fun getOrCreate(javaClass: Class<*>): Logger = loggers.getOrPut(javaClass) {
        LoggerFactory.getLogger(javaClass)
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
inline fun Any.logger() = LoggerMap.getOrCreate(this::class.java)