package com.ordersinkotlin.ordersinkotlin.seedwork

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

interface EventPublisher {
    suspend fun publish(domainEvent: DomainEvent)
}

suspend fun EventPublisher.publishAll(domainEvents: Iterable<DomainEvent>) = coroutineScope {
    val deferrals = domainEvents
        .map {
            async {
                publish(it)
            }
        }

    deferrals.awaitAll()
}

@Component
class SpringApplicationEventPublisher(val publisher: ApplicationEventPublisher) : EventPublisher {
    override suspend fun publish(domainEvent: DomainEvent) = publisher.publishEvent(domainEvent)
}