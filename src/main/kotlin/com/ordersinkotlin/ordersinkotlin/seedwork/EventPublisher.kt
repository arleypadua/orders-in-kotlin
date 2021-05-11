package com.ordersinkotlin.ordersinkotlin.seedwork

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

interface EventPublisher {
    fun publish(domainEvent: DomainEvent)
}

fun EventPublisher.publishAll(domainEvents: Iterable<DomainEvent>) {
    domainEvents
        .forEach { publish(it) }
}

@Component
class SpringApplicationEventPublisher(val publisher: ApplicationEventPublisher) : EventPublisher {
    override fun publish(domainEvent: DomainEvent) = publisher.publishEvent(domainEvent)
}